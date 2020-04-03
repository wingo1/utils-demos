package com.wingo1.example.weakreference;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public class WeakReferenceTest {

	public static void main(String[] args) {
		String cacheData = new String("hello");
		WeakReference<String> cacheRef = new WeakReference<>(cacheData);
		System.out.println("before gc:" + cacheRef.get());
		cacheData = null;
		System.gc();
		System.out.println("after gc:" + cacheRef.get());
		// weakhashmap
		Map<String, Integer> map = new WeakHashMap<>();
		map.put(new String("1"), 1);
		map.put(new String("2"), 1);
		map.put(new String("3"), 1);
		map.put("4", 1);
		System.out.println("before:" + map + ",size:" + map.size());
		System.gc();
		System.out.println("after:" + map + ",size:" + map.size());
	}

}
