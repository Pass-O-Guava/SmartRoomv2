/**
 * 
 * Example: REST Server for "GET POST PUT DELETE" Methods
 * 
 **/
package action;

import java.util.Map;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import com.opensymphony.xwork2.ModelDriven;

import pojo.Arduino;

public class RestController implements ModelDriven<Object> {
	
	private String ID; // Struts2：http://localhost:7777/wangye/api/arduino/* ID自动获取*
	
	public String getId() {
		return ID;
	}

	public void setId(String ID) {
		this.ID = ID;
	}
	
	//通过sql增删改查获得返回Object类型的arduinos
	public Arduino arduino;
	private Object arduinos;
	
	public Arduino getArduino(){
		return arduino;
	}

	public void setArduino(Arduino arduino){
		this.arduino = arduino;
	}

	public void setArduinos(Map<String, Arduino> arduinos) {
		this.arduinos = arduinos;
	}
	
	//getModel()
	public Object getModel() {
		if(arduinos == null) {
			return arduino;	
		} else {
			return arduinos;
		}
	}
	
	// GET	/api/arduino/110
	
	public HttpHeaders show() {
		//arduinos = ArduinoDao.selectByPrimaryKey(getId()); // database select
		System.out.println("GET \t /arduino/" + getId());
		return new DefaultHttpHeaders("show");
	}

	// GET	/api/users
	public HttpHeaders index() {
		//users = repository.getAll();
		System.out.println("GET \t /user");
		return new DefaultHttpHeaders("index");
	}

	// POST	/api/users
	public HttpHeaders create() {
		//users = repository.save(user);
		System.out.println("POST \t /user");
		return new DefaultHttpHeaders("create");
	}
	
	// PUT	/api/users
	public HttpHeaders update() {
		//users = repository.save(user);
		System.out.println("PUT \t /user");
		return new DefaultHttpHeaders("update");
	}
	
	// DELETE	/api/users/1
	public HttpHeaders destroy() {
		//users = repository.remove(getId());
		System.out.println("DELETE \t /user/{id}");
		return new DefaultHttpHeaders("destroy");
	}
	
}
