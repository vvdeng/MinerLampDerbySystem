package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.vv.minerlamp.entity.User;
import com.vv.minerlamp.util.HibernateUtil;

public class UserDAO {
	public void save(Long userId,String name, String pwd,Integer priv) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
	
		try {
			transaction = session.beginTransaction();
			User user = new User();
			user.setId(userId);
			user.setName(name);
			user.setPwd(pwd);
			user.setPriv(priv);
			session.saveOrUpdate(user);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<User> listUsers() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<User> users = new ArrayList<User>();
		try {
			transaction = session.beginTransaction();
			users = session.createQuery("from User").list();

			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return users;
	}

	public void updateUser(Long userId, String userName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			User user = (User) session.get(User.class, userId);
			user.setName(userName);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteUser(Long userId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			User user = (User) session.get(User.class, userId);
			session.delete(user);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	public User queryByName(String name) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		User result;

		User user = new User();
		user.setName(name);

		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Example.create(user));
		result = (User)criteria.uniqueResult();

		session.close();
		return result;
	}
	public User checkLogin(String name, String pwd) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		User result;

		User user = new User();
		user.setName(name);
		user.setPwd(pwd);
		Criteria criteria = session.createCriteria(User.class);
		criteria.add(Example.create(user));
		result = (User)criteria.uniqueResult();

		session.close();
		return result;
	}
}
