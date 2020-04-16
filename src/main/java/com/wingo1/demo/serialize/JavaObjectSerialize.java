package com.wingo1.demo.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

public class JavaObjectSerialize {

	public static void main(String[] args) throws Exception {
		List<Integer> origin = Arrays.asList(1, 2, 3, 4);
		System.out.println(origin + ":" + System.identityHashCode(origin));
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutput output = new ObjectOutputStream(byteArrayOutputStream);
		output.writeObject(origin);

		byte[] middle = byteArrayOutputStream.toByteArray();

		ObjectInput input = new ObjectInputStream(new ByteArrayInputStream(middle));
		List result = (List) input.readObject();
		System.out.println(result + ":" + System.identityHashCode(result));

	}

}
