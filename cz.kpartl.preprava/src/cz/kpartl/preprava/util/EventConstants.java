package cz.kpartl.preprava.util;

public interface EventConstants {
	public String NEW_MAIL = EventConstants.class.getName().replace('.', '/') + "/NEW_MAIL";
	public String NEW_MAIL_TAG_FOLDER = "folder";
	public String NEW_MAIL_TAG_MAIL   = "mail";
}
