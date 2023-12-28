
import controller.StudentController;
import model.StudentDAO;
import model.StudentDAOImpl;
import view.StudentView;


public class Main {
    public static void main(String[] args) {
        StudentDAO studentDAO = new StudentDAOImpl(); // Implement StudentDAOImpl
        StudentController studentController = new StudentController(studentDAO);
        StudentView studentView = new StudentView(studentController);

        studentView.setVisible(true);
    }
}
