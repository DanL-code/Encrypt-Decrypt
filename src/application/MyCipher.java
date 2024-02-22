package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Base64;
import java.util.Optional;

import javax.crypto.SecretKey;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MyCipher extends Application {
	private TextField usernameField;
	private PasswordField passwordField;
	private Label statusLabel;
	private Dialog<String> dialog;
	private BorderPane root = new BorderPane();
	private Scene scene = new Scene(root, 1000, 750);
	private String masterKey;
	private Algorithms algorithm = new Algorithms();
	
	private DataModule dataModule = new DataModule();

	private MenuButton dbChooserBtn;
	private String chosenDB;

	@Override
	public void start(Stage primaryStage) {
		loginScene(primaryStage);
		scene.getStylesheets().add(getClass().getResource("defaultTheme.css").toExternalForm());
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

	// Create login page
	public void loginScene(Stage primaryStage) {

		primaryStage.setTitle("Login Application");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
		grid.setHgap(10);

		Label usernameLabel = new Label("Username:");
		GridPane.setConstraints(usernameLabel, 0, 1);

		usernameField = new TextField();
		GridPane.setConstraints(usernameField, 1, 1);

		Label passwordLabel = new Label("Password:");
		GridPane.setConstraints(passwordLabel, 0, 2);

		passwordField = new PasswordField();
		GridPane.setConstraints(passwordField, 1, 2);

		Button loginButton = new Button("Login");
		GridPane.setConstraints(loginButton, 1, 3);
		loginButton.setOnAction(e -> {
			String userName = usernameField.getText();
			String password = passwordField.getText();
			if (userName != null && !userName.isEmpty() && password != null && !password.isEmpty()) {
				String inputP = algorithm.hashPassword(password);
				dataModule.loginMethod(userName, password, inputP, new DataModuleListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						algorithm.showConfirmation(primaryStage, "Login Successful.");
						mainScene(primaryStage, userName);
					}

					@Override
					public void onFaild(String msg) {
						// TODO Auto-generated method stub
						statusLabel.setText(msg);
					}

					@Override
					public void onError(String msg) {
						// TODO Auto-generated method stub
						algorithm.showErrorInfo(primaryStage, msg);
					}
					
				});
			} else {
				statusLabel.setText("Username doesn't exist, please create an account");
			}
		});

		Button createButton = new Button("Create a New Account");
		GridPane.setConstraints(createButton, 1, 4);
		createButton.setOnAction(e -> {
			if (dbChooserBtn.getText().equals("Choose Your Database")) {
				algorithm.showErrorInfo(primaryStage, "Please choose your database.");
			} else {
				createAccountScene(primaryStage);
			}
		});

		// choose different database
		dbChooserBtn = new MenuButton("Choose Your Database");
		GridPane.setConstraints(dbChooserBtn, 1, 0);
		dbChooserBtn.setMinWidth(250);
		MenuItem localDb = new MenuItem("Local Host Database");
		MenuItem cloudDb = new MenuItem("Cloud Database");
		dbChooserBtn.getItems().addAll(localDb, cloudDb);
		
		localDb.setOnAction(event -> {
			dbChooserBtn.setText("Local Host Database");
			dataModule.setJDBC_URL("jdbc:mysql://127.0.0.1:3306/Security");
			dataModule.setUSERNAME("root");
			dataModule.setPASSWORD("");
			chosenDB = "Local Host";
		});
		cloudDb.setOnAction(event -> {
			dbChooserBtn.setText("Cloud Database");
			dataModule.setJDBC_URL("jdbc:mysql://my-aws-db.*******/Security");
			dataModule.setUSERNAME("admin");
			dataModule.setPASSWORD("adminisdan");
			chosenDB = "Cloud";
		});

		statusLabel = new Label("");
		GridPane.setConstraints(statusLabel, 1, 5);

		// Add components to the grid
		grid.getChildren().addAll(usernameLabel, usernameField, passwordLabel, passwordField, loginButton, createButton,
				statusLabel, dbChooserBtn);
		grid.setAlignment(Pos.CENTER);
		Label l = new Label();
		root.setCenter(grid);
		root.setTop(l);

	}

	// create new account page
	public void createAccountScene(Stage primaryStage) {

		primaryStage.setTitle("Create Your Account");
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);
		grid.setHgap(10);

		Label chosenDBLabe = new Label("Creat an account in " + chosenDB + ".");
		GridPane.setConstraints(chosenDBLabe, 1, 0);
		Label usernameLabel = new Label("New Username:");
		GridPane.setConstraints(usernameLabel, 0, 1);

		usernameField = new TextField();
		GridPane.setConstraints(usernameField, 1, 1);
		usernameField.setPromptText("To be remembered for login");

		Label passwordLabel = new Label("New Password:");
		GridPane.setConstraints(passwordLabel, 0, 2);

		passwordField = new PasswordField();
		GridPane.setConstraints(passwordField, 1, 2);
		passwordField.setPromptText("To be remembered for login");

		Button createBtn = new Button("Create");
		GridPane.setConstraints(createBtn, 1, 3);
		createBtn.setOnAction(e -> {
			String newUserName = usernameField.getText();
			String newPassword = passwordField.getText();
			String encryptPassword = algorithm.hashPassword(newPassword);
			if (newUserName != null && !newUserName.isEmpty() && newPassword != null && !newPassword.isEmpty()) {
				dataModule.createAccountMethod(newUserName, newPassword, encryptPassword, new DataModuleListener() {

					@Override
					public void onSuccess(String msg) {
						// TODO Auto-generated method stub
						statusLabel.setText("Account created successfully");
						loginScene(primaryStage);
						algorithm.showConfirmation(primaryStage, "Created Successfully.");
		
					}

					@Override
					public void onFaild(String msg) {
						// TODO Auto-generated method stub
						algorithm.showErrorInfo(primaryStage, "Username already exists. Please choose a different username.");
						
					}

					@Override
					public void onError(String msg) {
						// TODO Auto-generated method stub
						
					}
					
				});
			}else {
				algorithm.showErrorInfo(primaryStage, "Please make sure username and password are filled.");
			}
			
			
		});

		Button backBtn = new Button("Back to Login");
		GridPane.setConstraints(backBtn, 1, 4);
		backBtn.setOnAction(event -> {
			loginScene(primaryStage);
			dbChooserBtn.setText(chosenDB + " Database");
		});

		statusLabel = new Label("");
		GridPane.setConstraints(statusLabel, 1, 5);
		grid.getChildren().addAll(chosenDBLabe, usernameLabel, usernameField, passwordLabel, passwordField, createBtn,
				statusLabel, backBtn);
		grid.setAlignment(Pos.CENTER);
		root.setCenter(grid);
	}

	// create main page
	public void mainScene(Stage primaryStage, String userName) {
		try {

			// Setup encrypt gridPane
			dataModule.loadTheme(userName, new DataModuleListener() {

				@Override
				public void onSuccess(String msg) {
					// TODO Auto-generated method stub
					scene.getStylesheets().add(getClass().getResource(msg).toExternalForm());
				}

				@Override
				public void onFaild(String msg) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onError(String msg) {
					// TODO Auto-generated method stub
					
				}
				
			});
			primaryStage.setTitle("🔢Encryption & Decryption🔢");
			DESSimple des1 = new DESSimple();
			AESSimple aes1 = new AESSimple();
			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(10);
			grid.setHgap(10);

			GridPane encryptGrid = new GridPane();
			encryptGrid.setPadding(new Insets(10, 10, 10, 10));
			encryptGrid.setVgap(10);
			encryptGrid.setHgap(10);

			Label encryptHeadline = new Label("Message Encryption");
			GridPane.setConstraints(encryptHeadline, 0, 0);

			MenuButton cipherBtn = new MenuButton("Pick an Algorithm");
			MenuItem caesarBtn = new MenuItem("Caesar Cipher");
			MenuItem desBtn = new MenuItem("DES");
			MenuItem aesBtn = new MenuItem("AES");
			cipherBtn.getItems().addAll(caesarBtn, desBtn, aesBtn);

			Button resetBtn = new Button("Reset");

			Label encryptIndexLabel = new Label("Input Your Index:");
			GridPane.setConstraints(encryptIndexLabel, 0, 2);
			TextField encryptIndexField = new TextField();
			encryptIndexField.setPromptText("To be Remembered while Decryption.");
			GridPane.setConstraints(encryptIndexField, 0, 3);
			Label encryptPlainLabel = new Label("Input Your Massage:");
			GridPane.setConstraints(encryptPlainLabel, 0, 4);
			TextArea encryptPlainField = new TextArea();
			GridPane.setConstraints(encryptPlainField, 0, 5);

			Button encryptSubmitBtn = new Button("Submit");
			GridPane.setConstraints(encryptSubmitBtn, 0, 6);
			Label encryptKeyLabel = new Label("Your Key:");
			GridPane.setConstraints(encryptKeyLabel, 0, 7);
			TextField encryptKeyField = new TextField();
			encryptKeyField.setPromptText("To be Remembered while Decryption.");
			GridPane.setConstraints(encryptKeyField, 0, 8);
			Button saveKeyBtn = new Button("Save the Key to File");
			GridPane.setConstraints(saveKeyBtn, 0, 9);

			// set Menu Button actions
			caesarBtn.setOnAction(event -> {
				cipherBtn.setText("Caesar Cipher");
				GridPane.setConstraints(encryptKeyLabel, 0, 6);
				GridPane.setConstraints(encryptKeyField, 0, 7);
				encryptPlainField.setPromptText(
						"Please enter exclusively English letters, numbers or special characters. Caesar Cipher may encounter difficulties processing characters beyond this scope.");
				GridPane.setConstraints(saveKeyBtn, 0, 9);
				encryptKeyField.setPromptText("Please enter a number. To be Remembered while Decryption.");
				encryptKeyLabel.setText("Input Your Key");
				GridPane.setConstraints(encryptSubmitBtn, 0, 8);
			});
			desBtn.setOnAction(event -> {
				cipherBtn.setText("DES");
				GridPane.setConstraints(encryptKeyLabel, 0, 7);
				GridPane.setConstraints(encryptKeyField, 0, 8);
				encryptPlainField.setPromptText("");
				encryptKeyField.setPromptText("To be Remembered while Decryption.");
				GridPane.setConstraints(encryptSubmitBtn, 0, 6);
				GridPane.setConstraints(saveKeyBtn, 0, 9);
				encryptKeyLabel.setText("Your Key");
			});
			aesBtn.setOnAction(event -> {
				cipherBtn.setText("AES");
				GridPane.setConstraints(encryptKeyLabel, 0, 7);
				GridPane.setConstraints(encryptKeyField, 0, 8);
				encryptPlainField.setPromptText("");
				encryptKeyField.setPromptText("To be Remembered while Decryption.");
				GridPane.setConstraints(encryptSubmitBtn, 0, 6);
				GridPane.setConstraints(saveKeyBtn, 0, 9);
				encryptKeyLabel.setText("Your Key");
			});

			// encrypt submit button
			encryptSubmitBtn.setOnAction(event -> {
				String indexStr = encryptIndexField.getText();
				String plainText = encryptPlainField.getText();

				if (indexStr != null && !indexStr.isEmpty() && plainText != null && !plainText.isEmpty()
						&& !cipherBtn.getText().equals("Pick an Algorithm")) {
					Boolean exist = dataModule.checkIndex(userName, indexStr);
					if (!exist) {
						if (cipherBtn.getText().equals("Caesar Cipher")) {
							int key = 0;
							try {
								key = Integer.parseInt(encryptKeyField.getText());
							} catch (Exception e) {
								algorithm.showErrorInfo(primaryStage, "Key should be a number, please enter a number.");
								return;
							}
							String result = algorithm.caesarCipherEncrypt(plainText, key);
//							encryptResultField.setText(result);
							String keyAsString = String.valueOf(key);
							String hashedKey = algorithm.hashPassword(keyAsString);
							String pickedAlgorithm = cipherBtn.getText();
							dataModule.storeMsg(userName, result, hashedKey, indexStr, pickedAlgorithm);
							algorithm.showConfirmation(primaryStage,
									"Encryption successful. Please keep the index and key secure for decryption. You may also save the key to a local file.");
						} else if (cipherBtn.getText().equals("DES")) {
							String result = algorithm.DESEncrypt(des1, plainText);
							SecretKey DESKey = des1.getSecretkey();
							String keyDES = Base64.getEncoder().encodeToString(DESKey.getEncoded());
							String hashedKey = algorithm.hashPassword(keyDES);
//							encryptResultField.setText(result);
							encryptKeyField.setText(keyDES);
							String pickedAlgorithm = cipherBtn.getText();
							dataModule.storeMsg(userName, result, hashedKey, indexStr, pickedAlgorithm);
							algorithm.showConfirmation(primaryStage,
									"Encryption successful. Please keep the index and key secure for decryption. You may also save the key to a local file.");

						} else if (cipherBtn.getText().equals("AES")) {
							String result = algorithm.AESEncrypt(aes1, plainText);
							SecretKey AESKey = aes1.getSecretKey();
							String keyAES = Base64.getEncoder().encodeToString(AESKey.getEncoded());
//							encryptResultField.setText(result);
							encryptKeyField.setText(keyAES);
							String hashedKey = algorithm.hashPassword(keyAES);
							String pickedAlgorithm = cipherBtn.getText();
							dataModule.storeMsg(userName, result, hashedKey, indexStr, pickedAlgorithm);
							algorithm.showConfirmation(primaryStage,
									"Encryption successful. Please keep the index and key secure for decryption. You may also save the key to a local file.");
						}
					} else {
						algorithm.showErrorInfo(primaryStage, "Index already exists. Please enter a different index.");
					}
				} else {
					algorithm.showErrorInfo(primaryStage,
							"Please make sure you have picked an algorithm and the index and message fields are filled.");
				}

			});

			// save key to file
			saveKeyBtn.setOnAction(event -> {
				String indexStr = encryptIndexField.getText();
				String savedKey = encryptKeyField.getText();
				masterKey = algorithm.getMasterKey();

				try {
					if (indexStr != null && !indexStr.isEmpty() && savedKey != null && !savedKey.isEmpty()) {
						String encryptSavedKey = algorithm.AESEncrypt(masterKey, savedKey);
						algorithm.saveKeyFile(indexStr, encryptSavedKey);
						algorithm.showConfirmation(primaryStage, "Successfully saved.");
					} else {
						algorithm.showErrorInfo(primaryStage,
								"Please make sure you have encrypted your message and all the required fields are completed.");
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			});

			// reset encryption part
			resetBtn.setOnAction(event -> {
				cipherBtn.setText("Pick an Algorithm");
				encryptIndexField.clear();
				encryptPlainField.clear();
				encryptPlainField.setPromptText(" ");
				encryptKeyField.clear();
				encryptKeyField.setPromptText(" ");
			});

			// organise page layout
			HBox topBox = new HBox();
			topBox.setSpacing(10);
			topBox.getChildren().addAll(resetBtn, cipherBtn);
			GridPane.setConstraints(topBox, 0, 1);

			encryptGrid.getChildren().addAll(encryptHeadline, topBox, encryptIndexLabel, encryptIndexField,
					encryptPlainLabel, encryptPlainField, encryptSubmitBtn, encryptKeyLabel, encryptKeyField,
					saveKeyBtn);

			// Setup decrypt gridPane
			GridPane decryptGrid = new GridPane();
			decryptGrid.setPadding(new Insets(10, 10, 10, 10));
			decryptGrid.setVgap(10);
			decryptGrid.setHgap(10);
			Label decryptHeadline = new Label("Message Decryption");
			GridPane.setConstraints(decryptHeadline, 1, 1);
			Button resetDecrypt = new Button("Reset");
			GridPane.setConstraints(resetDecrypt, 1, 2);

			Label decryptIndexLabel = new Label("Input Your Index:");
			GridPane.setConstraints(decryptIndexLabel, 1, 3);
			TextField decryptIndexField = new TextField();
			GridPane.setConstraints(decryptIndexField, 1, 4);
			decryptIndexField.setPromptText("Index for Decryption.");
			Label decryptKeyLabel = new Label("Input Your Key:");
			GridPane.setConstraints(decryptKeyLabel, 1, 5);
			TextField decryptKeyField = new TextField();
			decryptKeyField.setPromptText("Load the Key if You Saved it to File.");
			GridPane.setConstraints(decryptKeyField, 1, 6);

			Button decryptSubmitBtn = new Button("Submit");
			GridPane.setConstraints(decryptSubmitBtn, 1, 8);
			Button loadKeyBtn = new Button("Load the Key from File");
			GridPane.setConstraints(loadKeyBtn, 1, 7);
			Label messageLabel = new Label("Your Message:");
			GridPane.setConstraints(messageLabel, 1, 9);
			TextArea messageField = new TextArea();
			GridPane.setConstraints(messageField, 1, 10);

			Button logOutBtn = new Button("Logout");

			logOutBtn.setOnAction(event -> {
				loginScene(primaryStage);
			});

			// decrypt submit button
			decryptSubmitBtn.setOnAction(event -> {
				String decryptIndex = decryptIndexField.getText();
				String decryptKey = decryptKeyField.getText();
				String sqlMsg = dataModule.checkSQLMsg(userName, decryptIndex);
				String decryptAlgorithm = dataModule.checkChosenAlgorithm(userName, decryptIndex);
				if (decryptIndex != null && !decryptIndex.isEmpty() && decryptKey != null && !decryptKey.isEmpty()) {
					if (decryptAlgorithm.equals("Caesar Cipher")) {
						int caesarKey = Integer.parseInt(decryptKey);
						String result = algorithm.caesarCipherDecrypt(sqlMsg, caesarKey);
						messageField.setText(result);
					} else if (decryptAlgorithm.equals("DES")) {
						String result = algorithm.DESDecrypt(decryptKey, sqlMsg);
						messageField.setText(result);
					} else if (decryptAlgorithm.equals("AES")) {
						String result = algorithm.AESDecrypt(decryptKey, sqlMsg);
						messageField.setText(result);
					}
				} else {
					algorithm.showErrorInfo(primaryStage, "Please make sure the index and key are filled.");
				}
			});

			// load key from file
			loadKeyBtn.setOnAction(event -> {
				String decryptIndex = decryptIndexField.getText();
				masterKey = algorithm.getMasterKey();
				if (decryptIndex != null && !decryptIndex.isEmpty()) {
					String pw = getPasswordFromUser(primaryStage);
					String inputP = algorithm.hashPassword(pw);
					Boolean validated = dataModule.validatePassword(userName, pw, inputP);
					if (validated) {
						try {
							String loadedKey = algorithm.loadKeyFile(decryptIndex);
							String decryptLoadedKey = algorithm.AESDecrypt(masterKey, loadedKey);
							decryptKeyField.setText(decryptLoadedKey);
						} catch (Exception e) {
							algorithm.showErrorInfo(primaryStage, "No such key file, please enter the key manually.");
							e.printStackTrace();
							return;
						}
					} else {
						algorithm.showErrorInfo(primaryStage, "Wrong Password, please try again.");
					}
				} else {
					algorithm.showErrorInfo(primaryStage, "Please enter the index for decryption.");
				}
				
			});

			// reset decryption part
			resetDecrypt.setOnAction(event -> {
				decryptIndexField.clear();
				decryptKeyField.clear();
				messageField.clear();
			});

			decryptGrid.getChildren().addAll(resetDecrypt, decryptHeadline, decryptIndexLabel, decryptIndexField,
					decryptSubmitBtn, decryptKeyLabel, decryptKeyField, loadKeyBtn, messageLabel, messageField,
					logOutBtn);

			// theme setting
			MenuButton themeBtn = new MenuButton("Change Theme");
			MenuItem defaultBtn = new MenuItem("Default Theme");
			MenuItem greenBtn = new MenuItem("Green Leaf");
			MenuItem flowerBtn = new MenuItem("Daisy Flower");
			themeBtn.getItems().addAll(defaultBtn, greenBtn, flowerBtn);

			defaultBtn.setOnAction(event -> {
				themeBtn.setText("Default Theme");
				scene.getStylesheets().clear();
				scene.getStylesheets().add(getClass().getResource("defaultTheme.css").toExternalForm());
				dataModule.storeTheme(userName, "defaultTheme.css");
			});
			greenBtn.setOnAction(event -> {
				themeBtn.setText("Green Leaf");
				scene.getStylesheets().clear();
				scene.getStylesheets().add(getClass().getResource("greenTheme.css").toExternalForm());
				dataModule.storeTheme(userName, "greenTheme.css");
			});

			flowerBtn.setOnAction(event -> {
				themeBtn.setText("Daisy Flower");
				scene.getStylesheets().clear();
				scene.getStylesheets().add(getClass().getResource("flowerTheme.css").toExternalForm());
				dataModule.storeTheme(userName, "flowerTheme.css");
			});

			// organise page layout
			GridPane topRightPane = new GridPane();
			topRightPane.setPadding(new Insets(20, 10, 10, 10));
			topRightPane.setHgap(10);
			topRightPane.add(themeBtn, 0, 0);
			topRightPane.add(logOutBtn, 1, 0);
			topRightPane.setAlignment(Pos.TOP_RIGHT);
			GridPane.setConstraints(encryptGrid, 0, 1);
			GridPane.setConstraints(decryptGrid, 1, 1);
			grid.getChildren().addAll(encryptGrid, decryptGrid);
			grid.setAlignment(Pos.CENTER);
			root.setCenter(grid);
			root.setTop(topRightPane);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public String getPasswordFromUser(Stage primaryStage) {
		dialog = new Dialog<>();
		dialog.initOwner(primaryStage);
		dialog.setTitle("Password Input");
		dialog.setHeaderText("Enter Login Password to Load the Key:");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		dialog.getDialogPane().setContent(passwordField);
		
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		Platform.runLater(passwordField::requestFocus);
		dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return passwordField.getText();
            }
            return null;
        });
		Optional<String> result = dialog.showAndWait();
		return result.orElse(null);
	}

}
