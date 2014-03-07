package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vv.minerlamp.entity.LampChangeLog;
import com.vv.minerlamp.util.HibernateUtil;

public class LampChangeLogDAO {

	public List<LampChangeLog> listAll() {
		List<LampChangeLog> list = new ArrayList<LampChangeLog>();
		Session session = HibernateUtil.getSessionFactory().openSession();;
		list = session.createQuery("from LampChangeLog").list();
		return list;
	}
	public List<LampChangeLog> listAllforModel() {
		List<LampChangeLog> list = new ArrayList<LampChangeLog>();
		Session session = HibernateUtil.getSessionFactory().openSession();;
		list = session.createQuery("select log.rackId,log.lampNo,log.workId,log.staffName,log.changeTime from LampChangeLog as log").list();
		return list;
	}
	public void save(LampChangeLog lampChangeLog) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			session.save(lampChangeLog);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}


}
