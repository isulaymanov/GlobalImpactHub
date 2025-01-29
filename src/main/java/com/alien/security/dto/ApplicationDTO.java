package com.alien.security.dto;


import com.alien.security.entity.Application;
import com.alien.security.entity.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {
    private Long id;
    private String applicationNumber;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String approvalDate;
    private String rejectionReason;
    private Long eventId;

    public ApplicationDTO(Application application) {
        this.id = application.getId();
        this.applicationNumber = application.getApplicationNumber();
        this.status = application.getStatus();
        this.createdAt = application.getCreatedAt();
        this.updatedAt = application.getUpdatedAt();
        this.approvalDate = application.getApprovalDate();
        this.rejectionReason = application.getRejectionReason();
        this.eventId = application.getEvent().getId();    }

}
