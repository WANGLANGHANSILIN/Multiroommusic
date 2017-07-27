
package cn.com.multiroommusic.bean;

/**
 * Created by wang l on 2017/5/24.
 * 版本类对象
 */

public class MRMVersionInfoBean
{
	private String fileName;
	private String version;
  	private int		 mode;
  	private byte	 identifier;
  
	public String getFileName()
	{
		return fileName;
	}
	
	public void setFileName(String fileName)
	{
		this.fileName = fileName;
	}
  
	public String getVersion()
	{
		return version;
	}
	
	public void setVersion(String version)
	{
		this.version = version;
	}
	
	public int getMode()
	{
		return mode;
	}
	
	public void setMode(int mode)
	{
		this.mode = mode;
	}
	
	public byte getIdentifier()
	{
		return identifier;
	}
	
	public void setIdentifier(byte identifier)
	{
		this.identifier = identifier;
	}
}
