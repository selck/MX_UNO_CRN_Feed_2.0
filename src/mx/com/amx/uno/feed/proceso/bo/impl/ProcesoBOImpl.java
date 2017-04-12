package mx.com.amx.uno.feed.proceso.bo.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import mx.com.amx.uno.feed.proceso.bo.IProcesoBO;
import mx.com.amx.uno.feed.proceso.dto.CategoriaDTO;
import mx.com.amx.uno.feed.proceso.dto.NoticiaFeedDTO;
import mx.com.amx.uno.feed.proceso.dto.ParametrosDTO;
import mx.com.amx.uno.feed.proceso.dto.SeccionDTO;
import mx.com.amx.uno.feed.proceso.dto.TipoSeccionDTO;
import mx.com.amx.uno.feed.proceso.utils.Feed;
import mx.com.amx.uno.feed.proceso.utils.ObtenerProperties;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class ProcesoBOImpl implements IProcesoBO {
	
	public static String getDateZoneTime(String fechaString){
		String fecha="";
		try {
			//SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy hh:mm a");
	        Date date = formatter.parse(fechaString);
            
            TimeZone tz = TimeZone.getTimeZone("CST");
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			df.setTimeZone(tz);
			
			fecha=df.format(date);
		} catch (Exception e) {
			e.printStackTrace();
			return fechaString;
		}
		return fecha;
	}
	
	private final Logger LOG = Logger.getLogger(this.getClass().getName());

	
	String ambiente = "";	
	
	private  String URL_DAO = "";
	private final Properties props = new Properties();
	
	private RestTemplate restTemplate;
	HttpHeaders headers = new HttpHeaders();
	
	public ProcesoBOImpl() {
		super();
		restTemplate = new RestTemplate();
		ClientHttpRequestFactory factory = restTemplate.getRequestFactory();
		
		if ( factory instanceof SimpleClientHttpRequestFactory) {
			((SimpleClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000 );
			((SimpleClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000 );
			//LOG.info("Inicializando rest template");
		
		} else if ( factory instanceof HttpComponentsClientHttpRequestFactory) {
			((HttpComponentsClientHttpRequestFactory) factory).setReadTimeout( 35 * 1000);
			((HttpComponentsClientHttpRequestFactory) factory).setConnectTimeout( 35 * 1000);
			//LOG.info("Inicializando rest template");
		}
		
		restTemplate.setRequestFactory( factory );
		headers.setContentType(MediaType.APPLICATION_JSON);
	      
		try {
			props.load( this.getClass().getResourceAsStream( "/general.properties" ) );						
		} catch(Exception e) {
			LOG.error("[ConsumeWS::init]Error al iniciar y cargar arhivo de propiedades." + e.getMessage());
		}
		ambiente = props.getProperty("ambiente");
		URL_DAO = props.getProperty(ambiente+".ws.url.api.servicios");
		
	}
	
	public void procesoAutomatico() {
		LOG.info("************INI: GENERAR FEED *************");
		Feed getInfo = new Feed();
		getInfo.writeNewsML();
		if(ambiente!=null && ambiente.equalsIgnoreCase("desarrollo")){
			ObtenerProperties pro=new ObtenerProperties();
			ParametrosDTO parametros= pro.obtenerPropiedades();
			getInfo.transfiereWebServer(parametros.getPathShell(), parametros.getRutaCarpeta(), parametros.getRutaDestino());
		}
		LOG.info("************FIN: GENERAR FEED *************");
	}
	

	@Override
	public List<CategoriaDTO> getCategorias() {
		try{
			String url = URL_DAO + "getCategorias";
			return Arrays.asList(restTemplate.getForObject(url, CategoriaDTO[].class));
		}catch(Exception e){
			LOG.error("Error getCategorias [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<SeccionDTO> getSecciones() {
		try{
			String url = URL_DAO + "getSecciones";
			return Arrays.asList(restTemplate.getForObject(url, SeccionDTO[].class));
		}catch(Exception e){
			LOG.error("Error getSecciones [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<TipoSeccionDTO> getTipoSecciones() {
		try{
			String url = URL_DAO + "getTipoSecciones";
			return Arrays.asList(restTemplate.getForObject(url, TipoSeccionDTO[].class));
		}catch(Exception e){
			LOG.error("Error getTipoSecciones [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<NoticiaFeedDTO> getNotasByCategoria(String idCategoria, String numNotas, String fecha) {
		try{
			String url = URL_DAO + "getNotasByCategoria";
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idCategoria", idCategoria);
			parts.add("numNotas", numNotas);
			parts.add("fecha", fecha);
			//HttpEntity<String> entity = new HttpEntity<String>( idCategoria, headers);
			return Arrays.asList(restTemplate.postForObject(url,parts, NoticiaFeedDTO[].class));
			
		}catch(Exception e){
			LOG.error("Error getNotasByCategoria [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<NoticiaFeedDTO> getNotasBySeccion(String idSeccion, String numNotas, String fecha) {
		try{
			String url = URL_DAO + "getNotasBySeccion";
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idSeccion", idSeccion);
			parts.add("numNotas", numNotas);
			parts.add("fecha", fecha);
			//HttpEntity<String> entity = new HttpEntity<String>( idSeccion, headers);
			return Arrays.asList(restTemplate.postForObject(url,parts, NoticiaFeedDTO[].class));
			
		}catch(Exception e){
			LOG.error("Error getNotasBySeccion [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<NoticiaFeedDTO> getNotasByTipoSeccion(String idTipoSeccion, String numNotas, String fecha) {
		try{
			String url = URL_DAO + "getNotasByTipoSeccion";
			MultiValueMap<String, Object> parts;
			parts = new LinkedMultiValueMap<String, Object>();
			parts.add("idTipoSeccion", idTipoSeccion);
			parts.add("numNotas", numNotas);
			parts.add("fecha", fecha);
			//HttpEntity<String> entity = new HttpEntity<String>( idTipoSeccion, headers);
			return Arrays.asList(restTemplate.postForObject(url,parts, NoticiaFeedDTO[].class));
			
		}catch(Exception e){
			LOG.error("Error getNotasByTipoSeccion [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}

	@Override
	public List<NoticiaFeedDTO> getNotasByIdMagazine(String idMagazine) {
		try{
			String url = URL_DAO + "getNotasByIdMagazine";
			
			HttpEntity<String> entity = new HttpEntity<String>( idMagazine, headers);
			return Arrays.asList(restTemplate.postForObject(url,entity, NoticiaFeedDTO[].class));
			
		}catch(Exception e){
			LOG.error("Error getNotasByIdMagazine [BO]: " + e.getMessage() );
			return Collections.emptyList();
		}
	}
}
