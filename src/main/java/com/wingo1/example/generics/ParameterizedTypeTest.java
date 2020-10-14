package com.wingo1.example.generics;

import java.lang.reflect.ParameterizedType;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 获取泛型的类型， 必须是类的确定属性
 * 
 * @author cdatc-wingo1
 *
 */
public class ParameterizedTypeTest {
	List<Integer> list = new ArrayList<>();

	public static void main(String[] args) throws Exception {
		// list.getClass()....没有办法获取泛型的类型

		ParameterizedTypeTest instance = new ParameterizedTypeTest();
		ParameterizedType type = (ParameterizedType) instance.getClass().getDeclaredField("list").getGenericType();
		System.out.println(type.getActualTypeArguments()[0]);
		List<Double> list1 = new AbstractList<Double>() {

			@Override
			public Double get(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
		List<Float> list2 = new List<Float>() {

			@Override
			public int size() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public boolean isEmpty() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean contains(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Iterator<Float> iterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Object[] toArray() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public <T> T[] toArray(T[] a) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean add(Float e) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean remove(Object o) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean containsAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(Collection<? extends Float> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean addAll(int index, Collection<? extends Float> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean removeAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean retainAll(Collection<?> c) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void clear() {
				// TODO Auto-generated method stub

			}

			@Override
			public Float get(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Float set(int index, Float element) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void add(int index, Float element) {
				// TODO Auto-generated method stub

			}

			@Override
			public Float remove(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int indexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public int lastIndexOf(Object o) {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public ListIterator<Float> listIterator() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ListIterator<Float> listIterator(int index) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public List<Float> subList(int fromIndex, int toIndex) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		System.out.println(((ParameterizedType) list1.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		System.out
				.println(((ParameterizedType) list2.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0]);
	}

}
