module com.example.tank {
	requires javafx.controls;
	requires javafx.fxml;
	
	
	opens com.example.shooter to javafx.fxml;
	exports com.example.shooter;
}