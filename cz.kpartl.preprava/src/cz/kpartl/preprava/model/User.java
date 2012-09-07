package cz.kpartl.preprava.model;

import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.kpartl.preprava.util.CryptoUtils;


/**
 * @generated
 */
public class User implements java.io.Serializable {
	
	public static final String CONTEXT_NAME = "LOGGED_USER";
	
	final Logger logger = LoggerFactory.getLogger(User.class);		
	
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -980933042L;
	/**
	 * @generated
	 */
	private Long id;
	/**
	 * @generated
	 */
	private String username = "";
	/**
	 * @generated
	 */
	private String password = "";

	/**
	 * @generated
	 */
	private java.util.Set<Permission> permission = new java.util.HashSet<Permission>();

	/**
	 * @generated
	 */
	public User() {
	}

	/**
	 * @generated
	 */
	public Long getId() {
		return this.id;
	}

	/**
	 * @generated
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @generated
	 */
	public String getUsername() {
		return this.username;
	}

	/**
	 * @generated
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	
	
	/**
	 * @generated
	 */
	public String toString() {
		return "User" + " id=" + id + " username=" + username + " password="
				+ password;
	}

	/**
	 * @generated
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * @generated
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * @generated
	 */
	public java.util.Set<Permission> getPermission() {
		return permission;
	}

	/**
	 * @generated
	 */
	public void setPermission(java.util.Set<Permission> permission) {
		this.permission = permission;
	}

	/**
	 * @generated
	 */
	public void addPermission(Permission permission) {
		getPermission().add(permission);
	}

	/**
	 * @generated
	 */
	public void removePermission(Permission permission) {
		getPermission().remove(permission);
	}
}