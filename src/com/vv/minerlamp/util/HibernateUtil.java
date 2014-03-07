package com.vv.minerlamp.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;

import com.vv.minerlamp.entity.ChargingLog;
import com.vv.minerlamp.entity.InfoItem;
import com.vv.minerlamp.entity.LampChangeLog;
import com.vv.minerlamp.entity.LampRack;
import com.vv.minerlamp.entity.LampUnit;
import com.vv.minerlamp.entity.LedSetting;
import com.vv.minerlamp.entity.RackStatistics;
import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.entity.SysInf;
import com.vv.minerlamp.entity.User;

public class HibernateUtil {
	private static SessionFactory sessionFactory;
	static {
		try {
			sessionFactory = new AnnotationConfiguration()
					.addProperties(
							PropertiesUtil
									.getProperties(SysConfiguration.DBCONFIG_FILE_PATH))
					.configure().addPackage("com.vv.minerlamp.entity")
					.addAnnotatedClass(User.class)
					.addAnnotatedClass(Staff.class)
					.addAnnotatedClass(InfoItem.class)
					.addAnnotatedClass(LampRack.class)
					.addAnnotatedClass(LampUnit.class)
					.addAnnotatedClass(LedSetting.class)
					.addAnnotatedClass(LampChangeLog.class)
					.addAnnotatedClass(ChargingLog.class)
					.addAnnotatedClass(RackStatistics.class)
					.addAnnotatedClass(SysInf.class)
					.buildSessionFactory();
			;
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		getSessionFactory().close();
	}
}