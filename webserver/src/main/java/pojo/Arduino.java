package pojo;

public class Arduino {
	private String ip;
	private String version;
	private String a0;
	private String a1;
	private String a2;
	private String a3;
	private String a4;
	private String a5;
	private String d2;
	private String d3;
	private String d4;
	private String d5;
	private String d6;
	private String d7;
	private String d8;
	private String d9;
	private String d10;
	private String d11;
	private String d12;
	private String d13;

	//wy+
	public Arduino() {
		// TODO Auto-generated constructor stub
	}
	
	//wy+
	public Arduino(String ip, String version, 
			String a0, String a1, String a2, String a3, String a4, String a5,
			String d2, String d3, String d4, String d5, String d6, String d7, String d8, String d9, String d10, String d11, String d12, String d13) {
		this.ip = ip;
		this.version = version;
		this.a0 = a0;
		this.a1 = a1;
		this.a2 = a2;
		this.a3 = a3;
		this.a4 = a4;
		this.a5 = a5;
		this.d2 = d2;
		this.d3 = d3;
		this.d4 = d4;
		this.d5 = d5;
		this.d6 = d6;
		this.d7 = d7;
		this.d8 = d8;
		this.d9 = d9;
		this.d10 = d10;
		this.d11 = d11;
		this.d12 = d12;
		this.d13 = d13;
	}
	
	//wy+
	//private static Map<String, Arduino> arduinos = new HashMap<String,Arduino>();
	
	/*wy+
	public Map<String, Arduino> getAll() {
		arduinos.put("1", new Arduino("1.1.1.1", "version-1", "a0", "a1", "a2", "a3", "a4", "a5", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13" ));
		arduinos.put("2", new Arduino("2.2.2.2", "version-2", "a0-2", "a1", "a2", "a3", "a4", "a5", "d2", "d3", "d4", "d5", "d6", "d7", "d8", "d9", "d10", "d11", "d12", "d13" ));
		return arduinos;
	}
	*/
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version == null ? null : version.trim();
	}

	public String getA0() {
		return a0;
	}

	public void setA0(String a0) {
		this.a0 = a0 == null ? null : a0.trim();
	}

	public String getA1() {
		return a1;
	}

	public void setA1(String a1) {
		this.a1 = a1 == null ? null : a1.trim();
	}

	public String getA2() {
		return a2;
	}

	public void setA2(String a2) {
		this.a2 = a2 == null ? null : a2.trim();
	}

	public String getA3() {
		return a3;
	}

	public void setA3(String a3) {
		this.a3 = a3 == null ? null : a3.trim();
	}

	public String getA4() {
		return a4;
	}

	public void setA4(String a4) {
		this.a4 = a4 == null ? null : a4.trim();
	}

	public String getA5() {
		return a5;
	}

	public void setA5(String a5) {
		this.a5 = a5 == null ? null : a5.trim();
	}

	public String getD2() {
		return d2;
	}

	public void setD2(String d2) {
		this.d2 = d2 == null ? null : d2.trim();
	}

	public String getD3() {
		return d3;
	}

	public void setD3(String d3) {
		this.d3 = d3 == null ? null : d3.trim();
	}

	public String getD4() {
		return d4;
	}

	public void setD4(String d4) {
		this.d4 = d4 == null ? null : d4.trim();
	}

	public String getD5() {
		return d5;
	}

	public void setD5(String d5) {
		this.d5 = d5 == null ? null : d5.trim();
	}

	public String getD6() {
		return d6;
	}

	public void setD6(String d6) {
		this.d6 = d6 == null ? null : d6.trim();
	}

	public String getD7() {
		return d7;
	}

	public void setD7(String d7) {
		this.d7 = d7 == null ? null : d7.trim();
	}

	public String getD8() {
		return d8;
	}

	public void setD8(String d8) {
		this.d8 = d8 == null ? null : d8.trim();
	}

	public String getD9() {
		return d9;
	}

	public void setD9(String d9) {
		this.d9 = d9 == null ? null : d9.trim();
	}

	public String getD10() {
		return d10;
	}

	public void setD10(String d10) {
		this.d10 = d10 == null ? null : d10.trim();
	}

	public String getD11() {
		return d11;
	}

	public void setD11(String d11) {
		this.d11 = d11 == null ? null : d11.trim();
	}

	public String getD12() {
		return d12;
	}

	public void setD12(String d12) {
		this.d12 = d12 == null ? null : d12.trim();
	}

	public String getD13() {
		return d13;
	}

	public void setD13(String d13) {
		this.d13 = d13 == null ? null : d13.trim();
	}

	public String get(String foot) {
		switch (foot) {
		case "A0":
			return getA0();
		case "A1":
			return getA1();
		case "A2":
			return getA2();
		case "A3":
			return getA3();
		case "A4":
			return getA4();
		case "A5":
			return getA5();
		case "D2":
			return getD2();
		case "D3":
			return getD3();
		case "D4":
			return getD4();
		case "D5":
			return getD5();
		case "D6":
			return getD6();
		case "D7":
			return getD7();
		case "D8":
			return getD8();
		case "D9":
			return getD9();
		case "D10":
			return getD10();
		case "D11":
			return getD11();
		case "D12":
			return getD12();
		case "D13":
			return getD13();
		}
		return null;
	}
}