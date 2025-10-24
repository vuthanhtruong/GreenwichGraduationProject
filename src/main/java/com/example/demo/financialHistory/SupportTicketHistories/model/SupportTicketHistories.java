package com.example.demo.financialHistory.SupportTicketHistories.model;

import com.example.demo.supportTickets.model.SupportTickets;
import com.example.demo.accountBalance.model.AccountBalances;
import com.example.demo.financialHistory.financialHistories.model.FinancialHistories;
import com.example.demo.entity.Enums.Status;
import com.example.demo.user.student.model.Students;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SupportTicketHistories")
@PrimaryKeyJoinColumn(name = "HistoryID")
@Getter
@Setter
public class SupportTicketHistories extends FinancialHistories {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SupportTicketID", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SupportTickets supportTicket;

    @Column(name = "TicketTime", nullable = false)
    private LocalDateTime ticketTime;

    @Column(name = "Description", nullable = true, length = 1000)
    private String description;

    public SupportTicketHistories() {}

    public SupportTicketHistories(String historyId, Students student, SupportTickets supportTicket, AccountBalances accountBalance,
                                  LocalDateTime ticketTime, BigDecimal currentAmount, LocalDateTime createdAt, Status status, String description) {
        super(historyId, student, accountBalance, currentAmount,createdAt, status);
        this.supportTicket = supportTicket;
        this.ticketTime = ticketTime;
        this.description = description;
    }
}