package com.pythe.pojo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TblStudentEssayRecommendationExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public TblStudentEssayRecommendationExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNull() {
            addCriterion("student_id is null");
            return (Criteria) this;
        }

        public Criteria andStudentIdIsNotNull() {
            addCriterion("student_id is not null");
            return (Criteria) this;
        }

        public Criteria andStudentIdEqualTo(Long value) {
            addCriterion("student_id =", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotEqualTo(Long value) {
            addCriterion("student_id <>", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThan(Long value) {
            addCriterion("student_id >", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdGreaterThanOrEqualTo(Long value) {
            addCriterion("student_id >=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThan(Long value) {
            addCriterion("student_id <", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdLessThanOrEqualTo(Long value) {
            addCriterion("student_id <=", value, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdIn(List<Long> values) {
            addCriterion("student_id in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotIn(List<Long> values) {
            addCriterion("student_id not in", values, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdBetween(Long value1, Long value2) {
            addCriterion("student_id between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andStudentIdNotBetween(Long value1, Long value2) {
            addCriterion("student_id not between", value1, value2, "studentId");
            return (Criteria) this;
        }

        public Criteria andEssayIdIsNull() {
            addCriterion("essay_id is null");
            return (Criteria) this;
        }

        public Criteria andEssayIdIsNotNull() {
            addCriterion("essay_id is not null");
            return (Criteria) this;
        }

        public Criteria andEssayIdEqualTo(Long value) {
            addCriterion("essay_id =", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdNotEqualTo(Long value) {
            addCriterion("essay_id <>", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdGreaterThan(Long value) {
            addCriterion("essay_id >", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdGreaterThanOrEqualTo(Long value) {
            addCriterion("essay_id >=", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdLessThan(Long value) {
            addCriterion("essay_id <", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdLessThanOrEqualTo(Long value) {
            addCriterion("essay_id <=", value, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdIn(List<Long> values) {
            addCriterion("essay_id in", values, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdNotIn(List<Long> values) {
            addCriterion("essay_id not in", values, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdBetween(Long value1, Long value2) {
            addCriterion("essay_id between", value1, value2, "essayId");
            return (Criteria) this;
        }

        public Criteria andEssayIdNotBetween(Long value1, Long value2) {
            addCriterion("essay_id not between", value1, value2, "essayId");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeIsNull() {
            addCriterion("derive_time is null");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeIsNotNull() {
            addCriterion("derive_time is not null");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeEqualTo(Date value) {
            addCriterion("derive_time =", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeNotEqualTo(Date value) {
            addCriterion("derive_time <>", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeGreaterThan(Date value) {
            addCriterion("derive_time >", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("derive_time >=", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeLessThan(Date value) {
            addCriterion("derive_time <", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeLessThanOrEqualTo(Date value) {
            addCriterion("derive_time <=", value, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeIn(List<Date> values) {
            addCriterion("derive_time in", values, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeNotIn(List<Date> values) {
            addCriterion("derive_time not in", values, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeBetween(Date value1, Date value2) {
            addCriterion("derive_time between", value1, value2, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andDeriveTimeNotBetween(Date value1, Date value2) {
            addCriterion("derive_time not between", value1, value2, "deriveTime");
            return (Criteria) this;
        }

        public Criteria andFeedbackIsNull() {
            addCriterion("feedback is null");
            return (Criteria) this;
        }

        public Criteria andFeedbackIsNotNull() {
            addCriterion("feedback is not null");
            return (Criteria) this;
        }

        public Criteria andFeedbackEqualTo(Boolean value) {
            addCriterion("feedback =", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackNotEqualTo(Boolean value) {
            addCriterion("feedback <>", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackGreaterThan(Boolean value) {
            addCriterion("feedback >", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackGreaterThanOrEqualTo(Boolean value) {
            addCriterion("feedback >=", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackLessThan(Boolean value) {
            addCriterion("feedback <", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackLessThanOrEqualTo(Boolean value) {
            addCriterion("feedback <=", value, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackIn(List<Boolean> values) {
            addCriterion("feedback in", values, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackNotIn(List<Boolean> values) {
            addCriterion("feedback not in", values, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackBetween(Boolean value1, Boolean value2) {
            addCriterion("feedback between", value1, value2, "feedback");
            return (Criteria) this;
        }

        public Criteria andFeedbackNotBetween(Boolean value1, Boolean value2) {
            addCriterion("feedback not between", value1, value2, "feedback");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmIsNull() {
            addCriterion("derive_algorithm is null");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmIsNotNull() {
            addCriterion("derive_algorithm is not null");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmEqualTo(Integer value) {
            addCriterion("derive_algorithm =", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmNotEqualTo(Integer value) {
            addCriterion("derive_algorithm <>", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmGreaterThan(Integer value) {
            addCriterion("derive_algorithm >", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmGreaterThanOrEqualTo(Integer value) {
            addCriterion("derive_algorithm >=", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmLessThan(Integer value) {
            addCriterion("derive_algorithm <", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmLessThanOrEqualTo(Integer value) {
            addCriterion("derive_algorithm <=", value, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmIn(List<Integer> values) {
            addCriterion("derive_algorithm in", values, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmNotIn(List<Integer> values) {
            addCriterion("derive_algorithm not in", values, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmBetween(Integer value1, Integer value2) {
            addCriterion("derive_algorithm between", value1, value2, "deriveAlgorithm");
            return (Criteria) this;
        }

        public Criteria andDeriveAlgorithmNotBetween(Integer value1, Integer value2) {
            addCriterion("derive_algorithm not between", value1, value2, "deriveAlgorithm");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}