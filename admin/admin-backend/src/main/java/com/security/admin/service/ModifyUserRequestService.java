package com.security.admin.service;

import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.security.admin.dto.GenericMessageDTO;
import com.security.admin.dto.HospitalDTO;
import com.security.admin.dto.ModifyUserRequestDTO;
import com.security.admin.model.requests.ModifyUserRequest;
import com.security.admin.model.requests.RequestStatus;
import com.security.admin.pki.util.CryptographicUtility;
import com.security.admin.pki.util.KeyPairUtility;
import com.security.admin.pki.util.PEMUtility;
import com.security.admin.repository.ModifyUserRequestRepository;

@Service
public class ModifyUserRequestService {
	private ModifyUserRequestRepository repository;
	
	private HospitalService hospitalService;
	
	private String resourceFolderPath;
	
	private RestTemplate restTemplate;

	@Autowired
	public ModifyUserRequestService(@Value("${server.ssl.key-store-folder}") String resourceFolderPath,
			ModifyUserRequestRepository repository, HospitalService hospitalService, RestTemplate restTemplate) {
		this.repository = repository;
		this.hospitalService = hospitalService;
		this.resourceFolderPath = resourceFolderPath;
		this.restTemplate = restTemplate;
	}
	
	public void addRequest(@Valid ModifyUserRequestDTO dto) throws Exception {
		String commonName = dto.getHospitalName();
		HospitalDTO hospitalDTO = hospitalService.getByCommonName(commonName);
		String publicKeyPEM = hospitalDTO.getPublicKey();

		if (publicKeyPEM == null) {
			throw new Exception("Denied: Hospital with common name " + commonName + " not found. Contact a super admin to register this hospital's public key.");
		}

		// Verify signature
		byte[] csrBytes = dto.getCSRBytes();
		byte[] signature = Base64.getDecoder().decode(dto.getSignature());
		PublicKey publicKey = PEMUtility.PEMToPublicKey(publicKeyPEM);
		boolean valid = CryptographicUtility.verify(csrBytes, signature, publicKey);

		if (!valid) {
			throw new Exception("Denied: signature invalid.");
		}

		ModifyUserRequest request = new ModifyUserRequest(dto);

		save(request);
	}
	
	public void save(ModifyUserRequest req) {
		repository.save(req);
	}

	public ModifyUserRequestDTO getRequest(long id) {
		return toDTO(repository.getOne(id));
	}

	public List<ModifyUserRequestDTO> getAllRequestsDTO() {
		return repository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
	}
	
	private ModifyUserRequestDTO toDTO (ModifyUserRequest req) {
		return new ModifyUserRequestDTO(req);
	}

	public GenericMessageDTO confirmRequest(long id) throws IOException {
		ModifyUserRequest request = repository.getOne(id);
		request.setStatus(RequestStatus.SIGNED);
		repository.save(request);
		GenericMessageDTO response = sendRequestConfirmation(new ModifyUserRequestDTO(request));
		return response;
	}

	public GenericMessageDTO declineRequest(long id) throws IOException {
		ModifyUserRequest request = repository.getOne(id);
		request.setStatus(RequestStatus.REJECTED);
		repository.save(request);
		GenericMessageDTO response = sendRequestConfirmation(new ModifyUserRequestDTO(request));
		return response;
	}

	private GenericMessageDTO sendRequestConfirmation(ModifyUserRequestDTO dto) throws IOException {
		byte[] csrBytes = dto.getCSRBytes();
		PrivateKey privateKey = KeyPairUtility.readPrivateKey(resourceFolderPath + "/key.priv");
		byte[] signature = CryptographicUtility.sign(csrBytes, privateKey);
		String base64Signature = Base64.getEncoder().encodeToString(signature);
		dto.setSignature(base64Signature);

		HospitalDTO hospital = hospitalService.getByCommonName(dto.getHospitalName());
		String hospitalEndpointURI = hospital.getHospitalUrl() + "/api/modifyUserRequests/response";

		GenericMessageDTO csrResponse = this.restTemplate.postForObject(hospitalEndpointURI, dto,
				GenericMessageDTO.class);

		return csrResponse;
	}
}
