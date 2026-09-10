package com.wingo1.demo.javafx.spring.view;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.wingo1.demo.javafx.spring.dao.UserDao;
import com.wingo1.demo.javafx.spring.model.User;
import com.wingo1.demo.javafx.spring.utils.FXMLLoaderUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Scope("prototype")

@Component
public class RootController implements Initializable {

	@Resource
	private UserDao userDao;
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		logger.info("ROotController" + System.identityHashCode(this));
	}

	@FXML
	private void popDialog() throws IOException {
		Dialog<List<String>> dialog = new Dialog<>();
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setTitle("hello");
		dialog.setResizable(true);
		dialog.setHeaderText("Look, a Custom Login Dialog");
		GridPane pane = FXMLLoaderUtils.load(getClass().getResource("Dialog.fxml"));
		dialog.getDialogPane().setContent(pane);
		ButtonType buttonType = new ButtonType("注册(新增记录)");
		dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);
		Button lookupButton = (Button) dialog.getDialogPane().lookupButton(buttonType);
		lookupButton.setDefaultButton(true);
		dialog.setResultConverter(_buttonType -> {
			List<String> list = new ArrayList<>();
			if (_buttonType.equals(buttonType)) {
				Set<Node> lookupAll = dialog.getDialogPane().lookupAll(".text-field");
				for (Node node : lookupAll) {
					TextField textField = (TextField) node;
					list.add(textField.getText());
				}
			}
			return list;
		});
		Optional<List<String>> showAndWait = dialog.showAndWait();
		showAndWait.ifPresent(list -> {
			if (list.size() == 0) {
				return;
			}
			User user = new User();
			user.setName(list.get(0));
			user.setAge(Integer.valueOf(list.get(1)));
			DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse(list.get(2), ofPattern);
			user.setBirthDate(localDate);
			userDao.insertUser(user);
			Alert alert = new Alert(AlertType.INFORMATION, "result:" + list, new ButtonType[] {});
			alert.showAndWait();
		});
	}

	@FXML
	private void popNewStage() throws IOException {
		BorderPane pane = FXMLLoaderUtils.load(getClass().getResource("NewStage.fxml"));
		Stage newStage = new Stage();
		newStage.setScene(new Scene(pane));
		newStage.setTitle("new Stage");
		newStage.initModality(Modality.APPLICATION_MODAL);
		newStage.initStyle(StageStyle.DECORATED);
		Button lookup = (Button) pane.lookup(".button");

		TableView<User> tableView = (TableView<User>) pane.lookup(".table-view");
		ObservableList<TableColumn<User, ?>> columns = tableView.getColumns();
		columns.get(0).setCellValueFactory(new PropertyValueFactory<>("name"));
		columns.get(1).setCellValueFactory(new PropertyValueFactory<>("age"));
		columns.get(2).setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		ObservableList<User> personData = FXCollections.observableArrayList();
		List<User> selectUser = userDao.selectUser();
		for (User user : selectUser) {
			personData.add(user);
		}
		tableView.setItems(personData);
		lookup.addEventHandler(ActionEvent.ACTION, evt -> {
			newStage.close();
		});
		Button cacel = (Button) pane.lookup("#cancel");
		cacel.addEventHandler(ActionEvent.ACTION, evt -> {
			newStage.close();
		});
		newStage.showAndWait();

	}

	@FXML
	private void changeScene(Event evt) throws IOException {
		Button source = (Button) evt.getSource();
		if ("root1".equals(source.getScene().getUserData())) {// 变为2
			BorderPane pane = FXMLLoaderUtils.load(getClass().getResource("Root2.fxml"));
			Scene scene = new Scene(pane);
			Stage stage = (Stage) source.getScene().getWindow();
			stage.setScene(scene);
		} else {
			BorderPane pane = FXMLLoaderUtils.load(getClass().getResource("Root1.fxml"));
			Scene scene = new Scene(pane);
			scene.setUserData("root1");
			Stage stage = (Stage) source.getScene().getWindow();
			stage.setScene(scene);
		}
	}
}
