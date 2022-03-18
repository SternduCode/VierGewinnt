module com.sterndu.VierGewinnt {
	exports com.sterndu.viergewinnt;

	requires transitive javafx.base;
	requires transitive javafx.controls;
	requires transitive javafx.fxml;
	requires transitive javafx.graphics;

	opens com.sterndu.viergewinnt to javafx.graphics, javafx.fxml;
}