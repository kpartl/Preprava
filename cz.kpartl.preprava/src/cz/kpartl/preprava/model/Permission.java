package cz.kpartl.preprava.model;


/**
 * @generated
 */
public class Permission implements java.io.Serializable {
	/**
	 * @generated
	 */
	private static final long serialVersionUID = -520891886L;
	/**
	 * @generated
	 */
	private Long id;
	/**
	 * @generated
	 */
	private String name;
	/**
	 * @generated
	 */
	private String text;
	/**
	 * @generated
	 */
	public Permission() {
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
	public String getName() {
		return this.name;
	}

	/**
	 * @generated
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @generated
	 */
	public String getText() {
		return this.text;
	}

	/**
	 * @generated
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * @generated
	 */
	public String toString() {
		return "Permission" + " id=" + id + " name=" + name + " text=" + text;
	}
}