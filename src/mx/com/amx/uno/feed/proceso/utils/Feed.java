package mx.com.amx.uno.feed.proceso.utils;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;



import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import mx.com.amx.uno.feed.proceso.bo.IProcesoBO;
import mx.com.amx.uno.feed.proceso.bo.impl.ProcesoBOImpl;
import mx.com.amx.uno.feed.proceso.dto.CategoriaDTO;
import mx.com.amx.uno.feed.proceso.dto.SeccionDTO;
import mx.com.amx.uno.feed.proceso.dto.TipoSeccionDTO;
import mx.com.amx.uno.feed.proceso.dto.NoticiaFeedDTO;
import mx.com.amx.uno.feed.proceso.dto.ParametrosDTO;

public class Feed {
	
	private static final Logger log = Logger.getLogger(Feed.class);
	
	private List<NoticiaFeedDTO> feedList;
	private ParametrosDTO parametros;

	private IProcesoBO procesoBO;
	
	public static void main(String[] args){
		try {
			Feed getInfo = new Feed();
			getInfo.writeNewsML();
		} catch (Exception e) {
			System.out.println("Error main"+e);
		}
	}
	
	public Feed(){
		ObtenerProperties props = new ObtenerProperties();
		parametros = props.obtenerPropiedades();
	}
	 
	public org.w3c.dom.Document writeNewsML() {
		log.info(".: Ejecutandose...");
		procesoBO = new ProcesoBOImpl();
		try{
			log.info(":::: [INI] generamos archivos por categorias ::::");
			List<CategoriaDTO> lst = procesoBO.getCategorias();
			
			for(CategoriaDTO dto : lst){
				generarXML(dto.getFriendlyURL()+".xml", dto.getIdCategoria(), "categoria");
			}
			log.info(":::: [FIN] generamos archivos por categorias ::::");
			log.info(":::: [INI] generamos archivos por seccion ::::");
			List<SeccionDTO> lstSecciones = procesoBO.getSecciones();
			for(SeccionDTO dto : lstSecciones){
				generarXML(dto.getFriendlyURL()+"sec.xml", dto.getIdSeccion(), "seccion");
			}
			log.info(":::: [FIN] generamos archivos por seccion ::::");
			log.info(":::: [INI] generamos archivos por tipo seccion ::::");
			List<TipoSeccionDTO> lstTipoSecciones = procesoBO.getTipoSecciones();
			for(TipoSeccionDTO dto : lstTipoSecciones){
				generarXML(dto.getFriendlyURL()+".xml", dto.getIdTipoSeccion(), "tipoSeccion");
			}
			log.info(":::: [FIN] generamos archivos por tipo seccion ::::");
			
			log.info(":::: [INI] generamos archivos NoticiasMagazine ::::");
				generarXML("magazine.xml", parametros.getId_magazine_home(), "magazine");
			log.info(":::: [FIN] generamos archivos NoticiasMagazine ::::");
		
			log.info(":::: [INI] actualizamos monitoreo");
			EscribeArchivoMonitoreo.escribeArchivoMon(parametros);
			log.info(":::: [FIN] actualizamos monitoreo");
			
		} catch ( Exception e ){
			log.error("Error writeNewsML: ",e);
		}
		
		return null;
	}
	 
	 private void generarXML(String nombreArchivo, String nombreSeccion, String tipo) {
		try {
			if(tipo.equalsIgnoreCase("seccion")){
				feedList = getNotasBySeccion(nombreSeccion,"10","");
			} else if(tipo.equalsIgnoreCase("tipoSeccion")){
				feedList = getNotasByTipoSeccion(nombreSeccion,"10","");
			} else if(tipo.equalsIgnoreCase("magazine")){
				feedList = getNotasByIdMagazine(nombreSeccion);
			} else{
				feedList = getNotasByCategoria(nombreSeccion,"10","");
			}
			
			if ((this.feedList != null) && (this.feedList.size() > 0)) {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document docXML = docBuilder.newDocument();
				Element rootElement = docXML.createElement("NewsML");
				rootElement.setAttribute("xmlns:mi", "http://schemas.ingestion.microsoft.com/common/");
				docXML.appendChild(rootElement);
				

				Element catalog = docXML.createElement("Catalog");
				rootElement.appendChild(catalog);
				
				Element newsenv = docXML.createElement("NewsEnvelope");
				Element datime = docXML.createElement("DateAndTime");

				TimeZone tz = TimeZone.getTimeZone("America/Mexico_City");
		        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				df.setTimeZone(tz);
				Date date = new Date();
				String fechaS=df.format(date);
				fechaS=fechaS.substring(0, fechaS.length()-2)+":00";
				datime.appendChild(docXML.createTextNode(fechaS));
				
				newsenv.appendChild(datime);
				rootElement.appendChild(newsenv);
				
				for (int i = 0; i < this.feedList.size(); i++) {

					Element newsItem = docXML.createElement("NewsItem");
					rootElement.appendChild(newsItem);

					Element identification = docXML.createElement("Identification");
					Element newsIdentifier = docXML.createElement("NewsIdentifier");
					Element providerId = docXML.createElement("ProviderId");

					providerId.appendChild(docXML.createTextNode(this.parametros.getDominio()));

					Element newsItemId = docXML.createElement("NewsItemId");
					newsItemId.appendChild(docXML.createTextNode(this.feedList.get(i).getIdContenido()));

					newsIdentifier.appendChild(providerId);

					newsIdentifier.appendChild(newsItemId);
					identification.appendChild(newsIdentifier);
					newsItem.appendChild(identification);

					Element newsManagement = docXML.createElement("NewsManagement");
					Element newsItemType = docXML.createElement("NewsItemType");
					newsItemType.setAttribute("FormalName", "News");
					Element firstCreated = docXML.createElement("FirstCreated");
					
					
					String fechaPublicacion1=this.feedList.get(i).getFechaPublicacion();
					SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm a");
					Date date_parse1 = format.parse(fechaPublicacion1);
					fechaS=df.format(date_parse1);
					fechaS=fechaS.substring(0, fechaS.length()-2)+":00";
					firstCreated.appendChild(docXML.createTextNode(fechaS));
					Element thisRevisionCreated = docXML.createElement("ThisRevisionCreated");
					thisRevisionCreated.appendChild(docXML.createTextNode(fechaS));
					
					Element status = docXML.createElement("Status");
					status.setAttribute("FormalName", "Published");
					
					newsManagement.appendChild(newsItemType);
					newsManagement.appendChild(firstCreated);
					newsManagement.appendChild(thisRevisionCreated);
					newsManagement.appendChild(status);
					newsItem.appendChild(newsManagement);
					
					Element newsLines = docXML.createElement("NewsLines");
					Element copyrightLine = docXML.createElement("CopyrightLine");
					copyrightLine.appendChild(docXML.createTextNode("Derechos Reservados © Publicidad y Contenido Editorial S.A. de C.V."));
					
					Element headLine = docXML.createElement("HeadLine");
					headLine.appendChild(docXML.createTextNode(this.feedList.get(i).getTitulo()));
					
					Element slugLine = docXML.createElement("SlugLine");
					slugLine.appendChild(docXML.createTextNode(this.feedList.get(i).getTitulo()));
					
					Element byLine = docXML.createElement("ByLine");
					byLine.appendChild(docXML.createTextNode(this.feedList.get(i).getEscribio()));
					
					Element description = docXML.createElement("Description");
					description.appendChild(docXML.createTextNode(this.feedList.get(i).getDescripcion()));
					
					Element derivedFrom = docXML.createElement("DerivedFrom");
					derivedFrom.appendChild(docXML.createTextNode(this.feedList.get(i).getLinkNota()));
					
					newsLines.appendChild(copyrightLine);
					newsLines.appendChild(headLine);
					newsLines.appendChild(slugLine);
					newsLines.appendChild(byLine);
					newsLines.appendChild(description);
					newsLines.appendChild(derivedFrom);
					newsItem.appendChild(newsLines);
					
					Element newsComponent = docXML.createElement("NewsComponent");
					newsComponent.setAttribute("Duid", this.feedList.get(i).getIdContenido());
					
					Element newsComponent2 = docXML.createElement("NewsComponent");
					Element role = docXML.createElement("Role");
					role.setAttribute("FormalName", "Main");
					newsComponent2.appendChild(role);
					
					Element contentItem = docXML.createElement("ContentItem");
					
					Element mediaType = docXML.createElement("MediaType");
					mediaType.setAttribute("FormalName", "ComplexData");
					Element mimeType  = docXML.createElement("MimeType");
					mimeType.setAttribute("FormalName", "text/vnd.IPTC.NITF");
					
					contentItem.appendChild(mediaType);
					contentItem.appendChild(mimeType);
					
					Element dataContent = docXML.createElement("DataContent");
					Element nitf = docXML.createElement("nitf");
					Element body = docXML.createElement("body");
					Element bodyContent = docXML.createElement("body.content");
					
					
					if(this.feedList.get(i).getIdTipoNota().equals("video") || this.feedList.get(i).getIdTipoNota().equals("multimedia")){
						Element mediaVideo = docXML.createElement("media");
						mediaVideo.setAttribute("media-type", "video");
						
						Element mediaReferenceVideo = docXML.createElement("media-reference");
						mediaReferenceVideo.setAttribute("mime-type", "video/mp4");
						mediaReferenceVideo.setAttribute("source", this.feedList.get(i).getSourceVideo());
						mediaReferenceVideo.setAttribute("id", this.feedList.get(i).getIdContenido());
						mediaReferenceVideo.setAttribute("alternate-text", this.feedList.get(i).getAlternateTextVideo());
						mediaReferenceVideo.setAttribute("copyright", "PCE");
						mediaReferenceVideo.setAttribute("duration", this.feedList.get(i).getDurationVideo());
						mediaReferenceVideo.setAttribute("fileSize", this.feedList.get(i).getSizeVideo());
						
						Element mediaCaptionVideo = docXML.createElement("media-caption");
						mediaCaptionVideo.appendChild(docXML.createTextNode(this.feedList.get(i).getAlternateTextVideo()));
						
						mediaVideo.appendChild(mediaReferenceVideo);
						mediaVideo.appendChild(mediaCaptionVideo);
						bodyContent.appendChild(mediaVideo);
					}else{
						Element media = docXML.createElement("media");
						media.setAttribute("media-type", "image"); 
						
						Element mediaReference = docXML.createElement("media-reference");
						mediaReference.setAttribute("mime-type", "image/jpg");
						mediaReference.setAttribute("source", parametros.getDominio()+this.feedList.get(i).getImagenPrincipal());
						mediaReference.setAttribute("alternate-text", this.feedList.get(i).getImagenPie());
						
						Element mediaCaption = docXML.createElement("media-caption");
						mediaCaption.appendChild(docXML.createTextNode(this.feedList.get(i).getImagenPie()));	
						
						Element hasSyndicationRights = docXML.createElement("mi:hasSyndicationRights");
						hasSyndicationRights.appendChild(docXML.createTextNode("0"));
						
						media.appendChild(mediaReference);
						media.appendChild(mediaCaption);
						media.appendChild(hasSyndicationRights);
						bodyContent.appendChild(media);
					}
					
					String contenido=CodificaCaracteres.getEmbedPost(( this.feedList.get(i).getRtfContenido().replace("<strong>", "")));
					String contenidoS = contenido.replace("</strong>", "");
					String contenidoU = contenidoS.replace("<br />", "");
					String contenidoF = contenidoU.replace("&nbsp;", "");
					String contenidoFinal = contenidoF.replace("<p dir=\"ltr\">", "");
					bodyContent.appendChild(docXML.createCDATASection(contenidoFinal)); 
					
					body.appendChild(bodyContent);
					nitf.appendChild(body);
					dataContent.appendChild(nitf);
					
					contentItem.appendChild(dataContent);
					
					newsComponent2.appendChild(contentItem);
					Element item_keywords = docXML.createElement("item_keywords");
					Element itemkeyword = docXML.createElement("item_keyword");
					itemkeyword.appendChild(docXML.createTextNode(this.feedList.get(i).getKeywords()));
					item_keywords.appendChild(itemkeyword);
					
					newsComponent2.appendChild(item_keywords);
					
					List<NoticiaFeedDTO> listExtra3;
					String fechaPublicacion=this.feedList.get(i).getFechaPublicacion();
					
					Date date_parse = format.parse(fechaPublicacion);
					java.sql.Timestamp timeStampDate = new Timestamp(date_parse.getTime());
					
					if(tipo.equalsIgnoreCase("seccion")){
						listExtra3 = getNotasBySeccion(nombreSeccion, "3", timeStampDate.toString());
						//log.info("Notas Extras de tipo: seccion["+nombreSeccion+"] "+listExtra3.size());
					} else if(tipo.equalsIgnoreCase("tipoSeccion")){
						listExtra3 = getNotasByTipoSeccion(nombreSeccion, "3", timeStampDate.toString());
						//log.info("Notas Extras de tipo: tipoSeccion["+nombreSeccion+"] "+listExtra3.size());
					} else if(tipo.equalsIgnoreCase("magazine")){
						listExtra3 = getNotasByCategoria(this.feedList.get(i).getIdCategoria(), "3", timeStampDate.toString());
						//log.info("Notas Extras de tipo: magazine["+nombreSeccion+"] "+listExtra3.size());
					} else{
						listExtra3 = getNotasByCategoria(this.feedList.get(i).getIdCategoria(), "3", timeStampDate.toString());
						//log.info("Notas Extras de tipo: categoria["+nombreSeccion+"] "+listExtra3.size());
					}
					
					for (NoticiaFeedDTO nota : listExtra3) {
						Element associatedWith  = docXML.createElement("AssociatedWith");
						associatedWith.setAttribute("FormalName", "SeeAlso");
						associatedWith.setAttribute("NewsItem", nota.getLinkNota());
						
						Element comment1  = docXML.createElement("Comment");
						comment1.setAttribute("xml:lang", "es");
						comment1.setAttribute("FormalName", "Title");
						comment1.appendChild(docXML.createTextNode(nota.getTitulo()));
						
						Element comment2  = docXML.createElement("Comment");
						comment2.setAttribute("xml:lang", "es");
						comment2.setAttribute("FormalName", "Genre");
						comment2.appendChild(docXML.createTextNode(nota.getDescripcionCategoria()));
						
						
						Element media_image = docXML.createElement("media");
						media_image.setAttribute("media-type", "image");
						
						Element media_reference = docXML.createElement("media-reference");
						media_reference.setAttribute("mime-type", "image/jpeg");
						media_reference.setAttribute("source", parametros.getDominio() + nota.getImagenPrincipal());
						media_reference.setAttribute("alternate-text", nota.getImagenPie());
						media_reference.setAttribute("copyright", nota.getFuente());
						Element media_caption = docXML.createElement("media-caption");
						media_caption.appendChild(docXML.createTextNode(nota.getImagenPie()));
						
						media_image.appendChild(media_reference);
						media_image.appendChild(media_caption);
						
						associatedWith.appendChild(comment1);
						associatedWith.appendChild(comment2);
						associatedWith.appendChild(media_image);
						newsComponent2.appendChild(associatedWith);
						
					}
					newsComponent.appendChild(newsComponent2);
					newsItem.appendChild(newsComponent);
				}//fin del for
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				transformer.setOutputProperty("indent", "yes");
				transformer.setOutputProperty("encoding", "UTF-8");
				DOMSource source = new DOMSource(docXML);
				File file_to_write = new File(parametros.getRutaCarpeta() + nombreArchivo);
				StreamResult finalResult = new StreamResult(file_to_write);
				log.info(" Archivo  " + file_to_write.getAbsoluteFile());
				log.info("=================================================>");

				transformer.transform(source, finalResult);
			}
		} catch (Exception e) {
			log.error("Error generarXML: ",e);
		}
	}
	
	/*private static ArrayList<Element> insertaElementP(String cad, Document docXML) {
		ArrayList<Element> parrafos = new ArrayList();
		try {
			cad = filter(cad);
			String cadena = cad.trim();
			cadena = eliminaEspacios(cad);
			String[] temp = cadena.split("</p>");
			int auxi = 0;
			String aux = "";
			for (int i = 0; i < temp.length; i++) {
				aux = eliminaEspacios(temp[i]);
				aux.replace("strong", "");
				auxi = temp[i].trim().length();
				if (auxi > 0) {
					//log.info("AUXI: " + auxi);
					char car = temp[i].trim().charAt(auxi - 1);
					Element parrafo = docXML.createElement("p");
					if (car == '.') {
						parrafo.appendChild(docXML.createTextNode(aux));
					} else {
						parrafo.appendChild(docXML.createTextNode(aux + "."));
					}
					parrafos.add(parrafo);
				}
			}
		} catch (Exception e) {
			log.error("[ insertaElementP ] Error:" + e.getMessage(), e);
			e.printStackTrace();
			return null;
		}
		return parrafos;
	}
	
	public static String eliminaEspacios(String cad) {
		String cadena = "";
		cad = cad.trim();
		cadena = cad.replaceAll("\\s+", " ");
		cadena = cad.replaceAll("\t", "");
		cadena = cad.replaceAll("\t", "");
		cadena = cad.replaceAll("\n", "");
		cadena = cad.replace("^\\s+", "");
		cadena = cad.replace("\\s+$", "");
		return cadena.trim();
	}
	
	public static String filter(String str) {
		StringBuilder filtered = new StringBuilder(str.length());
		for (int i = 0; i < str.length(); i++) {
			char current = str.charAt(i);
			if (current == '�') {
				filtered.append("");
			} else{
				filtered.append(current);
			}
		}
		return filtered.toString();
	}*/
	
	public boolean transfiereWebServer(String rutaShell, String pathLocal, String pathRemote) {
		boolean success = false;

		String comando = "";
		  
		if(pathLocal.equals("") && pathRemote.equals("")){
			  comando = rutaShell;
		} else{
			  comando = rutaShell + " " + pathLocal+ "* " + pathRemote;
		}
		
		log.info("Comando:  " + comando);
		try {
			Runtime r = Runtime.getRuntime();
			Process p = r.exec(comando);
			success = true;
		} catch(Exception e) {
			success = false;
			log.error("Ocurrio un error al ejecutar el Shell " + comando + ": ", e);
		}
		return success;
	}
	
	 public List<NoticiaFeedDTO> getNotasByCategoria(String idMagazine, String numNotas, String fecha) throws Exception{
			procesoBO = new ProcesoBOImpl();
			return procesoBO.getNotasByCategoria(idMagazine, numNotas, fecha);
	 }
	 public List<NoticiaFeedDTO> getNotasBySeccion(String idSeccion, String numNotas, String fecha) throws Exception{
			procesoBO = new ProcesoBOImpl();
			return procesoBO.getNotasBySeccion(idSeccion, numNotas, fecha);
	 }
	 public List<NoticiaFeedDTO> getNotasByTipoSeccion(String idTipoSeccion, String numNotas, String fecha) throws Exception{
			procesoBO = new ProcesoBOImpl();
			return procesoBO.getNotasByTipoSeccion(idTipoSeccion, numNotas, fecha);
	 }
	 public List<NoticiaFeedDTO> getNotasByIdMagazine(String idMagazine) throws Exception{
			procesoBO = new ProcesoBOImpl();
			return procesoBO.getNotasByIdMagazine(idMagazine);
	 }

	/**
	 * @return the procesoBO
	 */
	public IProcesoBO getProcesoBO() {
		return procesoBO;
	}

	/**
	 * @param procesoBO the procesoBO to set
	 */
	@Autowired
	public void setProcesoBO(IProcesoBO procesoBO) {
		this.procesoBO = procesoBO;
	}
	
}