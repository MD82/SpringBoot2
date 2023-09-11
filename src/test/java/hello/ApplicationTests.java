package hello;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.*;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationTests {
	@Value("${api.google.appName}")
	private String appName;
	
	private final com.google.api.client.json.JsonFactory JSON_FACTORY
		= com.google.api.client.json.gson.GsonFactory.getDefaultInstance();
	
	private final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	
	@Test
	public void sheets() {
		assertThat(appName).isEqualTo("mayo-sheet");
	}
	
	@Test
	public void token() throws IOException {
		File initialFile = new File("src/main/resources/token.json");
		InputStream in = new FileInputStream(initialFile);
		
		if(in == null) {
			throw new FileNotFoundException("Resource file not found");
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			new NetHttpTransport(), JSON_FACTORY, clientSecrets, SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
			.setAccessType("offline")
			.build();
	}
}
