package com.wingo1.demo.javafx.spring.dao;

import java.util.List;

import org.apache.ibatis.annotations.Select;

import com.wingo1.demo.javafx.spring.model.User;

public interface UserDao {
	boolean insertUser(User user);

	@Select("select * from User")
	List<User> selectUser();

}
