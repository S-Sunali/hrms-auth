/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.adt.authservice.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.hibernate.annotations.NaturalId;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The type Role. Defines the role and the list of users who are associated with
 * that role
 */
@Entity(name = "ROLE")
@Table(catalog = "EmployeeDB", schema = "user_schema", name = "ROLE")
public class Role {

	@Id
	@Column(name = "ROLE_ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "ROLE_NAME")
	private String role;
	
	@Column(name = "DEFAULT_ROLE")
	private boolean defaultRole;
	
	@Transient
	private Set<String> permission;

	@ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER, cascade = { CascadeType.MERGE })
	@JsonIgnore
	private Set<User> userList = new HashSet<>();

	

	public Role() {

	}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public Set<User> getUserList() {
		return userList;
	}

	public void setUserList(Set<User> userList) {
		this.userList = userList;
	}


	public boolean isDefaultRole() {
		return defaultRole;
	}


	public void setDefaultRole(boolean defaultRole) {
		this.defaultRole = defaultRole;
	}


	public Set<String> getPermission() {
		return permission;
	}


	public void setPermission(Set<String> permission) {
		this.permission = permission;
	}
	
	
	
}
