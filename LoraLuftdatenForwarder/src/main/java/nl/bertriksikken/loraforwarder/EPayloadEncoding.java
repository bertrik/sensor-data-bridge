package nl.bertriksikken.loraforwarder;

public enum EPayloadEncoding {

	RUDZL("rudzl");
	
	private final String id;

	EPayloadEncoding(String id) {
		this.id = id;
	}
	
	String getId() {
		return id;
	}
	
	static EPayloadEncoding fromId(String id) {
		for (EPayloadEncoding encoding : values()) {
			if (encoding.id.equals(id)) {
				return encoding;
			}
		}
		return null;
	}
}
