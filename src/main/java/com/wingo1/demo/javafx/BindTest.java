package com.wingo1.demo.javafx;

import java.util.concurrent.TimeUnit;

import javafx.beans.Observable;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.util.Callback;

class Bill {

	// Define the property
	private DoubleProperty amountDue = new SimpleDoubleProperty();

	// Define a getter for the property's value
	public final double getAmountDue() {
		return amountDue.get();
	}

	// Define a setter for the property's value
	public final void setAmountDue(double value) {
		amountDue.set(value);
	}

	// Define a getter for the property itself
	public DoubleProperty amountDueProperty() {
		return amountDue;
	}

}

public class BindTest {

	public static void main(String[] args) {
		ObservableList<Bill> observableArrayList = FXCollections
				.observableArrayList(new Callback<Bill, Observable[]>() {

					@Override
					public Observable[] call(Bill param) {
						return new Observable[] { param.amountDueProperty() };
					}
				});
		observableArrayList.add(new Bill());
		observableArrayList.addListener(new ListChangeListener<Bill>() {

			@Override
			public void onChanged(Change<? extends Bill> c) {
				System.out.println(2);
			}

		});
		observableArrayList.add(new Bill());
		observableArrayList.get(0).setAmountDue(22);

		final DoubleProperty a = new SimpleDoubleProperty(1);
		final DoubleProperty b = new SimpleDoubleProperty(2);
		final DoubleProperty c = new SimpleDoubleProperty(3);
		final DoubleProperty d = new SimpleDoubleProperty(4);
		DoubleBinding db = new DoubleBinding() {

			{
				super.bind(a, b, c, d);
			}

			@Override
			protected double computeValue() {
				return (a.get() * b.get()) + (c.get() * d.get());
			}
		};
		System.out.println(db.get());
		b.set(3);
		System.out.println(db.get());
		Bill bill = new Bill();
		bill.setAmountDue(100);
		d.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> o, Number old, Number newone) {
				System.out.println("bill" + bill.getAmountDue());
			}
		});
		new Thread(() -> {
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			d.set(100);
		}).start();
	}

}
