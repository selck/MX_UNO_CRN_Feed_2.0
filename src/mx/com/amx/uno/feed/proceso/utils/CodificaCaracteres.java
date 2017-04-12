package mx.com.amx.uno.feed.proceso.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import mx.com.amx.uno.feed.proceso.dto.RedSocialEmbedPost;


public class CodificaCaracteres {
	
	private static Logger logger=Logger.getLogger(CodificaCaracteres.class);
	
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
			logger.error("Error getDateZoneTime: ",e);
			return fechaString;
		}
		return fecha;
	}
	
	private static String devuelveCadenasPost(String id_red_social, String rtfContenido){
		String url="", cadenaAReemplazar="", salida="";
		try {
			cadenaAReemplazar=rtfContenido.substring(rtfContenido.indexOf("["+id_red_social+"="), rtfContenido.indexOf("="+id_red_social+"]"))+"="+id_red_social+"]";
			url=cadenaAReemplazar.replace("["+id_red_social+"=", "").replace("="+id_red_social+"]", "");
			salida=cadenaAReemplazar+"|"+url;
		} catch (Exception e) {
			logger.error("Error devuelveCadenasPost: ",e);
			return "|";
		}
		return salida;
	}
	public static String getEmbedPost(String RTFContenido){
		try {
			String rtfContenido=RTFContenido;
			String url, cadenaAReemplazar;
			StringBuffer embedCode;
			HashMap<String,ArrayList<RedSocialEmbedPost>> MapAReemplazar = new HashMap<String,ArrayList<RedSocialEmbedPost>>();
			int num_post_embebidos;
			int contador;
			if(rtfContenido.contains("[instagram")){
				//logger.info("Embed Code instagram");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedInstagram=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[instagram=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("instagram", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode.append(" <div class=\"instagram-post\"> \n");
					embedCode.append(" <blockquote data-instgrm-captioned data-instgrm-version=\"6\" class=\"instagram-media\"> \n");
					embedCode.append(" <div> \n");
					embedCode.append(" 	<p><a href=\""+url+"\"></a></p> \n");
					embedCode.append(" </div> \n");
					embedCode.append(" </blockquote> \n");
					embedCode.append(" <script async defer src=\"//platform.instagram.com/en_US/embeds.js\"></script> \n");
					embedCode.append(" </div> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("instagram");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedInstagram.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("instagram", listRedSocialEmbedInstagram);
			}
			if(rtfContenido.contains("[twitter")){
				//logger.info("Embed Code twitter");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedTwitter=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[twitter=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("twitter", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode.append(" <div class=\"tweeet-post\"> \n");
					embedCode.append(" 		<blockquote data-width=\"500\" lang=\"es\" class=\"twitter-tweet\"><a href=\""+url+"\"></a></blockquote> \n");
					embedCode.append(" 		<script type=\"text/javascript\" async defer src=\"//platform.twitter.com/widgets.js\" id=\"twitter-wjs\"></script> \n");
					embedCode.append(" </div> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("twitter");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedTwitter.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("twitter", listRedSocialEmbedTwitter);
			
			}
			if(rtfContenido.contains("[facebook")){
				//logger.info("Embed Code facebook");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedFacebook=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[facebook=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("facebook", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode=new StringBuffer();
					embedCode.append(" <div class=\"facebook-post\"> \n");
					embedCode.append(" 		<div data-href=\""+url+"\" data-width=\"500\" class=\"fb-post\"></div> \n");
					embedCode.append(" </div> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("facebook");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedFacebook.add(embebedPost);
					contador++;;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("facebook", listRedSocialEmbedFacebook);
			}
			if(rtfContenido.contains("[giphy")){
				//logger.info("Embed Code giphy");
				ArrayList<RedSocialEmbedPost> listRedSocialEmbedGiphy=new ArrayList<RedSocialEmbedPost>();
				num_post_embebidos=rtfContenido.split("\\[giphy=").length-1;
				contador=1;
				do{
					RedSocialEmbedPost embebedPost=new RedSocialEmbedPost();
					String cadenas=devuelveCadenasPost("giphy", rtfContenido);
					cadenaAReemplazar=cadenas.split("\\|")[0];
					url=cadenas.split("\\|")[1];
					rtfContenido=rtfContenido.replace(cadenaAReemplazar, "");
					embedCode=new StringBuffer();
					embedCode=new StringBuffer();
					embedCode.append(" <img src=\""+url.split("\\,")[1]+"\" class=\"giphy\"> \n");
					embedCode.append(" <span>V�a  \n");
					embedCode.append(" 	<a href=\""+url.split("\\,")[0]+"\" target=\"_blank\">Giphy</a> \n");
					embedCode.append("  </span> \n");
					
					embebedPost.setCadena_que_sera_reemplazada(cadenaAReemplazar);
					embebedPost.setRed_social("giphy");
					embebedPost.setCodigo_embebido(embedCode.toString());
					
					listRedSocialEmbedGiphy.add(embebedPost);
					contador ++;
				}while(contador <= num_post_embebidos);
				
				MapAReemplazar.put("giphy", listRedSocialEmbedGiphy);
			}
			
			
			if(!MapAReemplazar.isEmpty()){
				Iterator<String> iterator_red_social = MapAReemplazar.keySet().iterator();
				String red_social="", codigo_embebido="", cadena_que_sera_reemplazada="";
				while(iterator_red_social.hasNext()){
					red_social = iterator_red_social.next();
			        if(red_social.equalsIgnoreCase("twitter") || red_social.equalsIgnoreCase("facebook") || red_social.equalsIgnoreCase("instagram") 
			        		|| red_social.equalsIgnoreCase("giphy")){
			        	ArrayList<RedSocialEmbedPost> listEmbebidos=MapAReemplazar.get(red_social);
			        	for (RedSocialEmbedPost redSocialEmbedPost : listEmbebidos) {
				        	cadena_que_sera_reemplazada=redSocialEmbedPost.getCadena_que_sera_reemplazada();
				        	codigo_embebido=redSocialEmbedPost.getCodigo_embebido();
				        	RTFContenido=RTFContenido.replace(cadena_que_sera_reemplazada, codigo_embebido);
						}
			        	
			        }
			    } 
			}
			
			return RTFContenido;
		} catch (Exception e) {
			logger.error("Error getEmbedPost: ",e);
			return RTFContenido;
		}
	}
	public String cambiaCaracteres2(String texto) {
		texto = texto.replaceAll("�", "&#225;");
        texto = texto.replaceAll("�", "&#233;");
        texto = texto.replaceAll("�", "&#237;");
        texto = texto.replaceAll("�", "&#243;");
        texto = texto.replaceAll("�", "&#250;");  
        texto = texto.replaceAll("�", "&#193;");
        texto = texto.replaceAll("�", "&#201;");
        texto = texto.replaceAll("�", "&#205;");
        texto = texto.replaceAll("�", "&#211;");
        texto = texto.replaceAll("�", "&#218;");
        texto = texto.replaceAll("�", "&#209;");
        texto = texto.replaceAll("�", "&#241;");        
        texto = texto.replaceAll("�", "&#170;");          
        texto = texto.replaceAll("�", "&#228;");
        texto = texto.replaceAll("�", "&#235;");
        texto = texto.replaceAll("�", "&#239;");
        texto = texto.replaceAll("�", "&#246;");
        texto = texto.replaceAll("�", "&#252;");    
        texto = texto.replaceAll("�", "&#196;");
        texto = texto.replaceAll("�", "&#203;");
        texto = texto.replaceAll("�", "&#207;");
        texto = texto.replaceAll("�", "&#214;");
        texto = texto.replaceAll("�", "&#220;");
        texto = texto.replaceAll("�", "&#191;");
        texto = texto.replaceAll("�", "&#8220;");        
        texto = texto.replaceAll("�", "&#8221;");
        texto = texto.replaceAll("�", "&#8216;");
        texto = texto.replaceAll("�", "&#8217;");
        texto = texto.replaceAll("�", "...");	        
		return texto;
	}
	
	public String cambiaAcentos2(String texto) {
		texto = texto.replaceAll("�", "a");
        texto = texto.replaceAll("�", "e");
        texto = texto.replaceAll("�", "i");
        texto = texto.replaceAll("�", "o");
        texto = texto.replaceAll("�", "u");  
        texto = texto.replaceAll("�", "A");
        texto = texto.replaceAll("�", "E");
        texto = texto.replaceAll("�", "I");
        texto = texto.replaceAll("�", "O");
        texto = texto.replaceAll("�", "U");
        texto = texto.replaceAll("�", "N");
        texto = texto.replaceAll("�", "n");        
        texto = texto.replaceAll("�", "");          
        texto = texto.replaceAll("�", "a");
        texto = texto.replaceAll("�", "e");
        texto = texto.replaceAll("�", "i");
        texto = texto.replaceAll("�", "o");
        texto = texto.replaceAll("�", "u");    
        texto = texto.replaceAll("�", "A");
        texto = texto.replaceAll("�", "E");
        texto = texto.replaceAll("�", "I");
        texto = texto.replaceAll("�", "O");
        texto = texto.replaceAll("�", "U");
        texto = texto.replaceAll("%", "");        
		return texto;
	}

}
