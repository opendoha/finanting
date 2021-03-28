package fr.finanting.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Group model
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GROUPS")
@Data
public class Group extends MotherPersistant {

	@Column(name = "GROUP_NAME", nullable = false, unique = true)
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ADMIN", nullable = false, unique = true)
    private User userAdmin;

    @OneToMany(mappedBy = "group")
    private List<Account> accounts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable( name = "USERS_GROUPS_ASSOCICATIONS",
                joinColumns = { 
                    @JoinColumn(name = "GROUP_ID_JOIN") }, inverseJoinColumns = { 
                    @JoinColumn(name = "USER_ID_INVERSE") })
    private List<User> users = new ArrayList<>();

}