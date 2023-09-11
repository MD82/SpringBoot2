package hello;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

public class SheetsQuickstartTest {
	private static final GsonFactory GSON_FACTORY = GsonFactory.getDefaultInstance();
	private final String TOKEN_DIRECTORY_PATH = "tokens";
	
	private final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
	private final String CREDENTIALS_FILE_PATH = "/credentials.json";
	
	private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
		InputStream in = SheetsQuickstartTest.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if(in == null) {
			throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
		}
		
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(GSON_FACTORY, new InputStreamReader(in));
		
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
			HTTP_TRANSPORT, GSON_FACTORY, clientSecrets, SCOPES)
			.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKEN_DIRECTORY_PATH)))
			.setAccessType("offline")
			.build();
		LocalServerReceiver reciver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, reciver).authorize("user");
	}
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		final String APPICATION_NAME = "Google Sheets API Quickstart";
		final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		final String spreadsheetId = "1m82_eCSnI1efScomBAlPbxNMXPB02OGeLrEsAAx57Io"; // "google spreadsheet에 데이터 쓰기"
		final String range = "data!A2:B100";
		Credential credential = new SheetsQuickstartTest().getCredentials(HTTP_TRANSPORT);
		Sheets service = new Sheets.Builder(HTTP_TRANSPORT, GSON_FACTORY, credential)
			.setApplicationName(APPICATION_NAME)
			.build();
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		List<List<Object>> values = response.getValues();
		if(values != null) {
			System.out.println("Name, Major");
			for(List<Object> row : values) {
				System.out.printf("%s, %s\n", row.get(0), row.get(1));
			}
		} else {
			System.out.println("No data found.");
		}
		
	}
}
