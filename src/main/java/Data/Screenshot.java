package Data;
/**
 * Created by Natsumi on 2014-05-21.
 */
public class Screenshot extends QuestionItem {
    private byte[] questionData;

    private Screenshot(int questionId,
                     byte[] questionData,
                     String questionAnswer,
                     int questionPoints,
                     boolean questionAnswered,
                     int questionTiming,
                     QuestionType questionCategory){
        super(questionId, questionAnswer, questionPoints, questionAnswered, questionTiming, questionCategory);
        this.questionData = questionData;
    }
}