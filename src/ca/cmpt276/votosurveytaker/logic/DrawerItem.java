package ca.cmpt276.votosurveytaker.logic;

/**
 * 
 * notification drawer item class
 *
 */
public class DrawerItem {
	private String title;
	private int imgResource;
	private boolean isHeader = false;
	
	public DrawerItem(String title, int imgResource, boolean isHeader) {
		this.title = title;
		this.imgResource = imgResource;
		this.isHeader = isHeader;
	}

	public boolean isHeader() {
		return isHeader;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getImgResource() {
		return imgResource;
	}

	public void setImgResource(int imgResource) {
		this.imgResource = imgResource;
	}
	
	public void setHeader(boolean isHeader) {
		this.isHeader = isHeader;
	}
}
