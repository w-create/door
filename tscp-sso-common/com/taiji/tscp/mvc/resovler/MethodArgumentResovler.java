package com.taiji.tscp.mvc.resovler;


/**
 * 自定义方法参数解析器
 * 
 * @author Joe
 */
public class MethodArgumentResovler {


	
	/**
	 * 参数的相关信息
	 */
	protected static class ParamInfo {


		private String name;

		
		public ParamInfo() {
			super();
		}
		

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
