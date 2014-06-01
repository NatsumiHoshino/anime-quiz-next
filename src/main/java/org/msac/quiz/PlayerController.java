package org.msac.quiz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.controlsfx.control.GridView;
import org.controlsfx.dialog.Dialogs;
import org.msac.data.*;

import java.io.IOException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Natsumi on 2014-05-24.
 */
public class PlayerController {
    @FXML protected FlowPane questionFlowPane;
    @FXML protected FlowPane screenshotFlowPane;
    @FXML protected FlowPane musicFlowPane;

    @FXML protected GridView<Question> questionGridView;
    @FXML protected GridView<Question> screenshotGridView;
    @FXML protected GridView<Question> musicGridView;

    @FXML protected Button addQuestionButton;
    @FXML protected Button addScreenshotButton;
    @FXML protected Button addMusicButton;

    private int setIndex;

    @FXML
    private void addQuestionButton_Click(){
        openEditorWindow(QuestionType.QUESTION);
        loadQuestions();
    }

    @FXML
    private void addScreenshotButton_Click(){
        openEditorWindow(QuestionType.SCREENSHOT);
        loadScreenshots();
    }

    @FXML
    private void addMusicButton_Click(){
        openEditorWindow(QuestionType.MUSIC);
        loadMusics();
    }

    public void init(int setIndex) {
        this.setIndex = setIndex;
        loadSet();
    }
    public void loadSet() {
        loadFromDatabase();

        loadQuestions();
        loadScreenshots();
        loadMusics();

        updateAddButtonVisibility();
    }

    private void loadFromDatabase() {
        try {
            Connection connection = DriverManager.getConnection(Main.getDbConnectionString());
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM Questions WHERE set_id = " + Main.setObservableList.get(setIndex).getSetId();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()) {
                QuestionType questionType = QuestionType.values()[resultSet.getInt("question_cat")];
                Question question = new Question(resultSet.getInt("question_id"),
                        resultSet.getBytes("question_data"),
                        resultSet.getString("question_answer"),
                        resultSet.getInt("question_points"),
                        resultSet.getBoolean("question_answered"),
                        resultSet.getInt("question_timing"),
                        questionType);
                switch (questionType) {
                    case QUESTION:
                        Main.setObservableList.get(setIndex).questionList.add(question);
                        break;
                    case SCREENSHOT:
                        Main.setObservableList.get(setIndex).screenshotList.add(question);
                        break;
                    case MUSIC:
                        Main.setObservableList.get(setIndex).musicList.add(question);
                        break;
                }
            }
        } catch (SQLException e) {
            Dialogs.create()
                    .owner(questionFlowPane.getScene().getWindow())
                    .title("Fail")
                    .masthead("An exception has occurred loading the database")
                    .message(e.getLocalizedMessage())
                    .showException(e);
        }
    }

    private void loadQuestions() {
        if(!Main.setObservableList.get(setIndex).questionList.isEmpty()){
            questionGridView.setItems(Main.setObservableList.get(setIndex).questionList);
            //questionGridView.setCellFactory(param -> new GridCell());
            //Logger.getAnonymousLogger().log(Level.INFO, "Bounded the question list to the grid view");
        }
    }

    private void loadScreenshots() {
        if(!Main.setObservableList.get(setIndex).screenshotList.isEmpty()) {
            screenshotGridView.setItems(Main.setObservableList.get(setIndex).screenshotList);
        }
    }

    private void loadMusics() {
        if(!Main.setObservableList.get(setIndex).musicList.isEmpty()){
            musicGridView.setItems(Main.setObservableList.get(setIndex).musicList);
        }
    }

    private void openEditorWindow(QuestionType questionType) {
        try {
            Stage editorStage = new Stage(StageStyle.UTILITY);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EditorWindow.fxml"));
            editorStage.setTitle(questionType.toString() + " Editor");
            editorStage.setScene(new Scene(loader.load(), 480, 240));
            loader.<EditorController>getController().init(questionType, Main.setObservableList.get(setIndex).getSetId());

            editorStage.setOnHiding(event -> loader.<EditorController>getController().close());
            editorStage.setOnCloseRequest(event -> loadSet());

            editorStage.showAndWait();
        } catch (IOException e) {
            Dialogs.create()
                    .owner(Main.getStage())
                    .title("Fail")
                    .masthead("An exception has occurred opening the editor")
                    .message(e.getLocalizedMessage())
                    .showException(e);
        }
    }

    /**
     * Update the visibility of the edit mode UI
     */
    public void updateAddButtonVisibility() {
        addQuestionButton.setVisible(Main.isEditMode());
        addScreenshotButton.setVisible(Main.isEditMode());
        addMusicButton.setVisible(Main.isEditMode());
    }
}
