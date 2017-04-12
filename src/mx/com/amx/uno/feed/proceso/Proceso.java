package mx.com.amx.uno.feed.proceso;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import mx.com.amx.uno.feed.proceso.bo.IProcesoBO;

public class Proceso implements ApplicationContextAware {

	private final Logger logger = Logger.getLogger(this.getClass().getName()); 
	private IProcesoBO procesoBO; 
	
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		logger.info(".: INICIO APLICACION : MX_UNO_CRN_Feed_2.0 :.") ;
	}

	public void procesoAutomatico() {
		try {
			procesoBO.procesoAutomatico();				
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
	
	public void procesoContentAutomatico() {
		try {
			procesoBO.procesoAutomatico();				
		} catch (Exception e) {
			logger.error("Exception", e);
		}
	}
	
	public IProcesoBO getProcesoBO() {
		return procesoBO;
	}

	public void setProcesoBO(IProcesoBO procesoBO) {
		this.procesoBO = procesoBO;
	}
	
}
