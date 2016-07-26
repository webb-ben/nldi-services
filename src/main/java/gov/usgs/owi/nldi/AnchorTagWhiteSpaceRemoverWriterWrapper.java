package gov.usgs.owi.nldi;

import org.apache.commons.io.output.StringBuilderWriter;

public class AnchorTagWhiteSpaceRemoverWriterWrapper extends StringBuilderWriter {
	private static final long serialVersionUID = -9155508330274732959L;

	/** 
	* The JTidy library adds a lot of white space. The Apache commons XML decoder does not fix
	* non-terminated tags while JTidy does. The source for these RSS has hyperlinks that look
	* as the following "(TLA)" and the additional space renders it as "( TLA)" and this class
	* is an attempt to refine these issues. Then we need to put back the space around the 
	* [menu] entries.
	* 
	* Furthermore, we also want external links to open in new tabs or windows. There is a REGEX
	* to locate &lt;a&gt; tags that contain HTTP in the HREF. The patter on the page is that
	* those that contain the protocol are external. The page contains some links that are to
	* areas on the same page and these should not load in a new window or tab.
	*/

	@Override
	public String toString() {
		// get the JTidy String
		String tmp = super.toString();

		// to fix the anchor tags we must first remove any leading white space added by JTidy
		tmp = tmp.replaceAll("\\s*<a", "<a");
		// then we need to put back the space for the [menu] entries
		tmp = tmp.replaceAll("\\[<a", "[ <a");

		// then remove any new lines within anchor tags added by JTidy as well so REGEX can process the HREF
		tmp = tmp.replaceAll("<a\\s*\\n", "<a ");
		// finally we need to add target="_blank" on pages that are not on the same page
		tmp = tmp.replaceAll("(?i)<a(.*href=\"http)", "<a target=\"_blank\" $1");

		return tmp;
	}

}
