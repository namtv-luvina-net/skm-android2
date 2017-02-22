package epsap4.soliton.co.jp;

public class SessionInfo {  
    private static SessionInfo instance = new SessionInfo();  
       
    private SessionInfo(){}  
       
    public static SessionInfo getInstance() {  
        return instance;  
    }  
      
    private boolean block = false;
    private boolean unlock = false;  
    private String packageName = null;
    private String className = null;
    private String extraType = null;
    private String extraString = null;
    
    public void setBlock(boolean b) {
    	block = b;
    }
      
    public void setUnlock(boolean b) {  
        unlock = b;  
    }
    
    public void setExtra(String pkg, String cls, String type, String str) {
    	packageName = pkg;
    	className = cls;
    	extraType = type;
    	extraString = str;
    }
    
    public void clear() {
    	block = false;
    	unlock = false;
    	packageName = null;
    	className = null;
    	extraType = null;
    	extraString = null;
    }
    
    public String getPackageName() {
    	return packageName;
    }
    
    public String getClassName() {
    	return className;
    }
    
    public String getExtraType() {
    	return extraType;
    }
    
    public String getExtraString () {
    	return extraString;
    }
    
    public boolean isBlock() {
    	return block;
    }
  
    public boolean isUnlock() {  
        return this.unlock;
    }
}