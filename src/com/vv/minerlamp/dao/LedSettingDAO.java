package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.vv.minerlamp.entity.LedSetting;
import com.vv.minerlamp.util.HibernateUtil;

public class LedSettingDAO {
	public void save(LedSetting ledSetting) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;

		try {
			transaction = session.beginTransaction();
			session.saveOrUpdate(ledSetting);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	public List<LedSetting> listLedSettings() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<LedSetting> ledSettings = new ArrayList<LedSetting>();
		try {
			transaction = session.beginTransaction();
			ledSettings = session.createQuery("from LedSetting").list();

			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return ledSettings;
	}

	public void updateLedSetting(Long ledSettingId, String ledSettingContent) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			LedSetting ledSetting = (LedSetting) session.get(LedSetting.class,
					ledSettingId);
			ledSetting.setContent(ledSettingContent);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteLedSetting(Long ledSettingId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			LedSetting ledSetting = (LedSetting) session.get(LedSetting.class,
					ledSettingId);
			session.delete(ledSetting);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public LedSetting queryByContent(String content) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		LedSetting result;

		LedSetting ledSetting = new LedSetting();
		ledSetting.setContent(content);

		Criteria criteria = session.createCriteria(LedSetting.class);
		criteria.add(Example.create(ledSetting));
		result = (LedSetting) criteria.uniqueResult();

		session.close();
		return result;
	}
}
