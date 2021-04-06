package com.security.admin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.security.admin.dto.CertificateSigningRequestDTO;
import com.security.admin.dto.GenericMessageDTO;
import com.security.admin.service.CertificateSigningRequestService;

import java.util.List;

@RestController
@RequestMapping(value = "/api/certificateRequests", produces = MediaType.APPLICATION_JSON_VALUE)
public class CertificateSigningRequestController {
    private CertificateSigningRequestService certificateSigningRequestService;

    @Autowired
    public CertificateSigningRequestController(CertificateSigningRequestService certificateRequestService) {
        this.certificateSigningRequestService = certificateRequestService;
    }

    @PostMapping()
    public ResponseEntity<GenericMessageDTO> requestNewCertificate(@RequestBody CertificateSigningRequestDTO dto) {
        certificateSigningRequestService.addRequest(dto);

        return new ResponseEntity<>(new GenericMessageDTO("Certificate request successfully created! Wait for the Super Admin to respond."), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CertificateSigningRequestDTO> getCertificateRequest(@PathVariable long id) {
    	CertificateSigningRequestDTO dto = certificateSigningRequestService.getRequestDTO(id);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<CertificateSigningRequestDTO>> getAllCertificateRequests() {
        List<CertificateSigningRequestDTO> dtos = certificateSigningRequestService.getAllRequestsDTO();

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}
