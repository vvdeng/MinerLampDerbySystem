package com.vv.minerlamp.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vv.minerlamp.entity.SysInf;
import com.vv.minerlamp.util.HibernateUtil;

public class SysInfDAO {
	public Long save(SysInf sysInf) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long infoItemId = null;
		try {
			transaction = session.beginTransaction();

			infoItemId = (Long) session.save(sysInf);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return infoItemId;
	}

	public Long update(SysInf sysInf) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long infoItemId = null;
		String hql = null;
		if (sysInf.getVal() != null) {
			hql = "update SysInf set val=" + sysInf.getVal() + " where cat="
					+ sysInf.getCat();
		}
		else if (sysInf.getDescr() != null) {
			hql = "update SysInf set descr='" + sysInf.getDescr() + "' where cat="
					+ sysInf.getCat();
		}
		try {
			transaction = session.beginTransaction();
			Query query = session.createQuery(hql);
			query.executeUpdate();
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
	public List<SysInf> listAll() {
		Session session = HibernateUtil.getSessionFactory().openSession();

		List<SysInf> sysInfoList = session.createQuery("from SysInf").list();
		System.out.println("sysInfoList.size()=" + sysInfoList.size());
		session.close();

		return sysInfoList;
	}

}
