package com.example.demo.entity.Enums;

public enum ContractStatus {
    DRAFT,          // Contract created but not yet finalized
    PENDING,        // Submitted, waiting for review/approval
    APPROVED,       // Reviewed and approved
    REJECTED,       // Rejected during review
    ACTIVE,         // Currently valid and in effect
    EXPIRED,        // Reached the end date, no longer valid
    TERMINATED      // Ended early by one of the parties
}
