package fr.finanting.server.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;

import fr.finanting.server.model.embeddable.Address;
import fr.finanting.server.model.embeddable.BankDetails;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Account model
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ACCOUNTS")
@Data
public class Account extends MotherPersistant {
    
	@Column(name = "ACCOUNT_LABEL", nullable = false)
    private String label;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    private User user;

	@Column(name = "BANK_NAME", nullable = false)
    private String bankName;

	@Column(name = "ABBREVIATION", nullable = false, length = 6)
    private String abbreviation;

    @Column(name = "INITIAL_BALANCE", nullable = false)
    private Integer initialBalance = 0;

    @Embedded
    private BankDetails bankDetails;
    
    @Embedded
    private Address address;
}
