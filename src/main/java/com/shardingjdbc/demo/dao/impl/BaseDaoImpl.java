package com.shardingjdbc.demo.dao.impl;

import com.shardingjdbc.demo.dao.BaseDao;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

public abstract class BaseDaoImpl<T,ID extends Serializable> implements BaseDao<T,ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    @Override
    public List<T> findAll(String tablename) {
        String sql=" from "+tablename+" u ";
        Query query=entityManager.createQuery(sql);
        List<T> list= query.getResultList();
        entityManager.close();
        return list;
    }

    @Transactional
    @Override
    public boolean save(T entity){
        boolean flag=false;
       /* try {
            if (entity instanceof BaseEntity) {
                ((BaseEntity)entity).setCreateId(Currents.getUserId());
                ((BaseEntity)entity).setCreateTime(new Timestamp(System.currentTimeMillis()));
            }
            entityManager.persist(entity);
            flag=true;
        }catch (Exception e){
            throw e;
        }*/
        return flag;
    }
    @Transactional
    @Override
    public T findByid(T entity,ID id) {
        return (T)entityManager.find(entity.getClass(),id);
    }
    @Transactional
    @Override
    public List<T> findByHql(String tablename, String filed, Object o ) {
        String sql="from "+tablename+" u WHERE u."+filed+"=?";
        System.out.println(sql+"--------sql语句-------------");
        Query query=entityManager.createQuery(sql);
        query.setParameter(1,o);
        List<T> list= query.getResultList();
        entityManager.close();
        return list;
    }

    @Override
    public Object findObjiectByHql(String tablename, String filed, Object o) {
        String sql="from "+tablename+" u WHERE u."+filed+"=?";
        Query query=entityManager.createQuery(sql);
        query.setParameter(1,o);

        entityManager.close();
        return query.getSingleResult();
    }
    @Transactional
    @Override
    public List<T> findByMoreFiled(String tablename,LinkedHashMap<String,Object> map) {
        String sql="from "+tablename+" u WHERE ";
        Set<String> set=null;
        set=map.keySet();
        List<String> list=new ArrayList<>(set);
        List<Object> filedlist=new ArrayList<>();
        for (String filed:list){
            sql+="u."+filed+"=? and ";
            filedlist.add(filed);
        }
        sql=sql.substring(0,sql.length()-4);
        Query query=entityManager.createQuery(sql);
        for (int i=0;i<filedlist.size();i++){
            query.setParameter(i+1,map.get(filedlist.get(i)));
        }
        List<T> listRe= query.getResultList();
        entityManager.close();
        return listRe;
    }
    @Transactional
    @Override
    public List<T> findByMoreFiledPages(String tablename, LinkedHashMap<String,List<Map<String,Object>>> map, int start, int pageNumber) {
        StringBuffer hql= new StringBuffer("from "+tablename+" ");
        formatHql(hql,map);
        Query query=entityManager.createQuery(hql.toString());
        query.setFirstResult((start-1)*pageNumber);
        query.setMaxResults(pageNumber);
        List<T> listRe= query.getResultList();
        entityManager.close();
        return listRe;
    }
    @Transactional
    @Override
    public List<T> findPages(String tablename, String filed, Object o, int start, int pageNumer) {
        String sql="from "+tablename+" u WHERE u."+filed+"=?";
        List<T> list=new ArrayList<>();
        try {
            Query query=entityManager.createQuery(sql);
            query.setParameter(1,o);
            query.setFirstResult((start-1)*pageNumer);
            query.setMaxResults(pageNumer);
            list= query.getResultList();
            entityManager.close();
        }catch (Exception e){
            e.printStackTrace();
        }

        return list;
    }
    @Transactional
    @Override
    public boolean update(T entity) {
        boolean flag = false;
       /* try {
            if (entity instanceof BaseEntity) {
                ((BaseEntity)entity).setUpdateId(Currents.getUserId());
                ((BaseEntity)entity).setUpdateTime(new Timestamp(System.currentTimeMillis()));
            }
            entityManager.merge(entity);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return flag;
    }
    @Transactional
    @Override
    public Integer updateMoreFiled(String tablename, LinkedHashMap<String, Object> map) {
        String sql="UPDATE "+tablename+" AS u SET ";
        Set<String> set=null;
        set=map.keySet();
        List<String> list=new ArrayList<>(set);
        for (int i=0;i<list.size()-1;i++){
            if (map.get(list.get(i)).getClass().getTypeName()=="java.lang.String"){
                System.out.println("-*****"+map.get(list.get(i))+"------------"+list.get(i));
                sql+="u."+list.get(i)+"='"+map.get(list.get(i))+"' , ";
            }else {
                sql+="u."+list.get(i)+"="+map.get(list.get(i))+" , ";
            }
        }
        sql=sql.substring(0,sql.length()-2);
        sql+="where u.id=? ";
        System.out.println(sql+"--------sql语句-------------");
        int resurlt=0;
        try {
            Query query=entityManager.createQuery(sql);
            query.setParameter(1,map.get("id"));
            resurlt= query.executeUpdate();
        }catch (Exception e){
            System.out.println("更新出错-----------------------");
            e.printStackTrace();

        }
        return resurlt;
    }

    @Transactional
    @Override
    public boolean delete(T entity) {
        boolean flag=false;
        try {
            entityManager.remove(entityManager.merge(entity));
            flag=true;
        }catch (Exception e){
            System.out.println("---------------删除出错---------------");
        }
        return flag;
    }

    @Override
    public Object findCount(String tablename, LinkedHashMap<String,List<Map<String,Object>>> map) {
        StringBuffer hql = new StringBuffer("select count(1) from "+tablename+" ");
        formatHql(hql,map);
        Query query=entityManager.createQuery(hql.toString());
        return query.getSingleResult();
    }
    /**
     * 格式化hql 配置查询条件
     * */
    private static void formatHql(StringBuffer hql,LinkedHashMap<String,List<Map<String,Object>>> map){
        if(map!=null&&!map.isEmpty()){
            hql.append("u WHERE ");
            for (String operator:map.keySet()){
                List<Map<String,Object>> params =  map.get(operator);
                for (Map<String,Object> paramMap:params) {
                    for (String filedKey:paramMap.keySet()) {
                        if(operator.equals("like")||operator.equals("LIKE")){
                            hql.append("u."+filedKey+" "+operator+" '%"+paramMap.get(filedKey).toString()+"%' and ");
                        }else if(operator.equals("asc")||operator.equals("ASC")){
                            hql.append(" order by u."+filedKey+" "+operator+" and ");
                        }else if(operator.equals("desc")||operator.equals("DESC")){
                            hql.append(" order by u."+filedKey+" "+operator+" and ");
                        }else{
                            hql.append("u."+filedKey+" "+operator+" '"+paramMap.get(filedKey).toString()+"' and ");
                        }
                    }
                }
            }
            hql.delete(hql.length()-4,hql.length());
        }

    };


}