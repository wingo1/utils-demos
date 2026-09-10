package com.wingo1.demo.javafx;

import java.time.LocalDateTime;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.Lighting;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * 时间轴动画、时间轴事件、插值器Interpolator
 * 
 * @author cdatc-wingo1
 *
 */
public class TimelineTest extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("TimelineTest");
		Group group = new Group();
		primaryStage.setScene(new Scene(group, 800, 600));
		primaryStage.show();

		Rectangle rectangle = new Rectangle(50, 300, 50, 50);
		rectangle.setFill(Color.RED);
		rectangle.setEffect(new Lighting());
		group.getChildren().add(rectangle);

		Timeline timeline = new Timeline();
		KeyValue kValue = new KeyValue(rectangle.xProperty(), 600, Interpolator.EASE_BOTH);
		KeyFrame keyFrame = new KeyFrame(Duration.millis(1000), event -> {
			System.out.println(LocalDateTime.now().toString() + "-triggered!");
		}, new KeyValue(rectangle.scaleYProperty(), 2, Interpolator.EASE_BOTH), kValue);
		timeline.getKeyFrames().add(keyFrame);
		timeline.setAutoReverse(true);
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
	}

}
