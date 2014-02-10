package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.util.HibernateUtil;

public class InfoItemDAO {
	public Long save(String name, Integer type,String value) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long infoItemId = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = new InfoItem();
			infoItem.setName(name);
			infoItem.setType(type);
			infoItem.setValue(value);
			infoItemId = (Long) session.save(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItemId;
	}

	@SuppressWarnings("unchecked")
	public List<InfoItem> listInfoItems() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<InfoItem> infoItems = new ArrayList<InfoItem>();
		try {
			transaction = session.beginTransaction();
			infoItems = session.createQuery("from InfoItem").list();
		/*	for (InfoItem infoItem : infoItems) {
				System.out.println(infoItem.getName());
			}
		*/
			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItems;
	}

	public void updateInfoItem(Long infoItemId, String infoItemName) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = (InfoItem) session.get(InfoItem.class, infoItemId);
			infoItem.setName(infoItemName);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteInfoItem(Long infoItemId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			InfoItem infoItem = (InfoItem) session.get(InfoItem.class, infoItemId);
			session.delete(infoItem);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public List<InfoItem> qeueryByType(Integer type) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<InfoItem> result=new ArrayList<InfoItem>();

		InfoItem infoItem = new InfoItem();
		infoItem.setType(type);
		
		Criteria criteria = session.createCriteria(InfoItem.class);
		criteria.add(Example.create(infoItem));
		result = criteria.list();

		session.close();
		return result;
	}
}
