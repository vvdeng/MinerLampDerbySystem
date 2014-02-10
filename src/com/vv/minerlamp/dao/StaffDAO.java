package com.vv.minerlamp.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;

import com.vv.minerlamp.entity.Staff;
import com.vv.minerlamp.util.HibernateUtil;
import com.vv.minerlamp.util.StaffState;
import com.vv.minerlamp.util.SysConfiguration;

public class StaffDAO {
	public StaffDAO() {
		lampUnitDAO = new LampUnitDAO();
	}

	public Long save(Staff staff, Integer state, Integer action,
			String manufacturer) {

		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		Long staffId = null;
		try {
			transaction = session.beginTransaction();
			Staff checkStaff;
			if ((checkStaff = queryByLampNoandRackIdInCurrentSession(
					staff.getLampNo(), staff.getRackId(), session)) != null) {
				deleteStaff(checkStaff.getId());
			}
			staffId = (Long) session.save(staff);
			lampUnitDAO.setLampStateInCurrentSession(staff.getRackId(),
					staff.getLampNo(), state, action, manufacturer, session);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return staffId;
	}

	@SuppressWarnings("unchecked")
	public List<Staff> listStaffs() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		List<Staff> staffs = new ArrayList<Staff>();
		try {
			transaction = session.beginTransaction();
			staffs = session.createQuery("from Staff").list();
			for (Staff staff : staffs) {
				System.out.println(staff.getName());
			}
			transaction.commit();

		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return staffs;
	}

	public void updateStaffChargingLastTime(Staff staff, Date date) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			staff.setChargingLastTime(date);
			session.update(staff);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteStaff(Long staffId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Staff staff = (Staff) session.get(Staff.class, staffId);
			session.delete(staff);
			transaction.commit();
		} catch (HibernateException e) {
			transaction.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	public void deleteStaff(Long rackId, Long lampNo, Integer state,
			Integer action) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		try {
			transaction = session.beginTransaction();
			Query query = session
					.createQuery("delete from Staff where rackId=:rackId and lampNo=:lampNo");

			query.setLong("rackId", rackId);
			query.setLong("lampNo", lampNo);
			query.executeUpdate();
			lampUnitDAO.setLampStateInCurrentSession(rackId, lampNo, state,
					action, null, session);
			transaction.commit();
		} catch (HibernateException hibernateException) {
			transaction.rollback();
			hibernateException.printStackTrace();
		} finally {
			session.close();
		}

	}

	public void deleteStaffInCurrentSession(Long rackId, Long lampNo,
			Session session) {

		Query query = session
				.createQuery("delete from Staff where rackId=:rackId and lampNo=:lampNo");

		query.setLong("rackId", rackId);
		query.setLong("lampNo", lampNo);
		query.executeUpdate();

	}

	public Staff queryByLampNoandRackIdInCurrentSession(Long lampNo,
			Long rackId, Session session) {
		Staff result;

		Staff staff = new Staff();
		staff.setLampNo(lampNo);
		staff.setRackId(rackId);
		Criteria criteria = session.createCriteria(Staff.class);
		criteria.add(Example.create(staff));
		result = (Staff) criteria.uniqueResult();
		System.out
				.println("in session-----------------------------------------------"
						+ result + "r=" + rackId + "l=" + lampNo);
		return result;
	}

	public Staff queryByLampNoandRackId(Long lampNo, Long rackId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Staff result;

		Staff staff = new Staff();
		staff.setLampNo(lampNo);
		staff.setRackId(rackId);
		Criteria criteria = session.createCriteria(Staff.class);
		criteria.add(Example.create(staff));
		result = (Staff) criteria.uniqueResult();
		
		session.close();
		return result;
	}

	public List<Object[]> queryTimeoutModelByDepartmentId(Long departmentId) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Object[]> result;

		Query query = session
				.createQuery("select staff.workId,staff.name,staff.department,staff.chargingLastTime from Staff staff where staff.departmentId=:departmentId and staff.chargingLastTime is not null and (UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(staff.chargingLastTime)>="
						+ (SysConfiguration.timeOutTime + SysConfiguration.standardWorkTime)*3600*24
						+ ")");
		query.setLong("departmentId", departmentId);
		result = query.list();
		session.close();
		return result;
	}
	public List<Object[]> queryTimeoutDetailModel() {
		Session session = HibernateUtil.getSessionFactory().openSession();
		List<Object[]> result;

		Query query = session
				.createQuery("select staff.clazz,staff.name,staff.rackId,staff.lampNo,staff.department,staff.chargingLastTime from Staff staff where  staff.chargingLastTime is not null and (UNIX_TIMESTAMP(now())-UNIX_TIMESTAMP(staff.chargingLastTime)>="
						+ (SysConfiguration.timeOutTime + SysConfiguration.standardWorkTime)*3600
						+ ")");
		result = query.list();
		session.close();
		return result;
	}

	public List<Object[]> queryStaffInfoModel(Integer modelType, Long workId,
			String name, Long professionId, Long departmentId, Long rackId,
			Long lampNo) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		StringBuilder sb = null;
		switch (modelType) {
		case Staff.STAFF_MODEL_TYPE_1:
			sb = new StringBuilder(
					"select staff.workId,staff.name,staff.rackId,staff.lampNo,staff.profession,staff.department from Staff staff ");

			break;
		case Staff.STAFF_MODEL_TYPE_2:
			sb = new StringBuilder(
					"select staff.department,staff.workId,staff.name,staff.rackId,staff.lampNo,staff.profession,staff.chargingLastTime from Staff staff ,  LampUnit lampUnit   ");
		default:
			break;
		}

		List<Object[]> result = new ArrayList<Object[]>();
		StringBuilder querySb = new StringBuilder();
		if (workId != null && !workId .equals( 0L)) {
			querySb.append("staff.workId=" + workId);
		}
		if (name != null && !name.trim().isEmpty()) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.name='" + name+"'");
		}
		if (professionId != null && !professionId .equals( 0L)) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.professionId=" + professionId);
		}
		if (departmentId != null && !departmentId .equals( 0L)) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.departmentId=" + departmentId);
		}
		if (rackId != null && !rackId .equals( 0L)) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.rackId=" + rackId);
		}
		if (lampNo != null && !lampNo .equals( 0L)) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("staff.lampNo=" + lampNo);
		}
		if (modelType == Staff.STAFF_MODEL_TYPE_2) {
			if (querySb.length() > 0) {
				querySb.append(" and ");
			}
			querySb.append("lampUnit.state=" + StaffState.STAFF_UNDERGROUND)
					.append("and staff.rackId=lampUnit.rackId and staff.lampNo=lampUnit.lampNo");
		}
		if (querySb.length() > 0) {
			sb.append(" where ").append(querySb);
		}
		Query query = session.createQuery(sb.toString());
		result = query.list();
		session.close();
		return result;
	}

	public void deleteAllInCurrentSession(Session session) {
		Query query = session.createQuery("delete from Staff");
		query.executeUpdate();
	}

	private LampUnitDAO lampUnitDAO;
}
