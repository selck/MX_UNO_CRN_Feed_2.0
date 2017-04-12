package mx.com.amx.uno.feed.proceso.bo;

import java.util.List;

import mx.com.amx.uno.feed.proceso.dto.CategoriaDTO;
import mx.com.amx.uno.feed.proceso.dto.NoticiaFeedDTO;
import mx.com.amx.uno.feed.proceso.dto.SeccionDTO;
import mx.com.amx.uno.feed.proceso.dto.TipoSeccionDTO;

public interface IProcesoBO {

	void procesoAutomatico();
	
	public List<CategoriaDTO> getCategorias();
	
	public List<SeccionDTO> getSecciones();
	
	public List<TipoSeccionDTO> getTipoSecciones();
	
	public List<NoticiaFeedDTO> getNotasByCategoria(String idCategoria, String numNotas, String fecha);
	
	public List<NoticiaFeedDTO> getNotasBySeccion(String idSeccion, String numNotas, String fecha);
	
	public List<NoticiaFeedDTO> getNotasByTipoSeccion(String idTipoSeccion, String numNotas, String fecha);
	
	public List<NoticiaFeedDTO> getNotasByIdMagazine(String idMagazine);
	
	
}
