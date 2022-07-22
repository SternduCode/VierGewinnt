module com.sterndu.VierGewinnt {
	exports com.sterndu.viergewinnt;

	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml; 
	requires transitive javafx.graphics; 
	requires com.sterndu.MultiCore;
	requires com.sterndu.DataTransfer;
	requires java.desktop;
	requires javafx.media;

	opens com.sterndu.viergewinnt to javafx.graphics, javafx.fxml;
}