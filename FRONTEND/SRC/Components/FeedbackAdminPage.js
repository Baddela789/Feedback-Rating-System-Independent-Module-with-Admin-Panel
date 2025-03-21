import { Button, Form, Input, Switch, Table, notification, Card, Select } from 'antd';
import axios from 'axios';
import React, { useEffect, useState } from 'react';
import moment from 'moment';

const { Option } = Select;

const FeedbackAdminPage = () => {
  const [isFeedbackEnabled, setIsFeedbackEnabled] = useState(false);
  const [triggers, setTriggers] = useState([]);
  const [feedbacks, setFeedbacks] = useState([]);
  const [searchText, setSearchText] = useState('');
  const [filteredFeedbacks, setFilteredFeedbacks] = useState([]);
  const [dateFilter, setDateFilter] = useState(null);

  useEffect(() => {
    fetchTriggers();
    fetchFeedbacks();
    fetchFeedbackSystemStatus();
  }, []);

  useEffect(() => {
    setFilteredFeedbacks(feedbacks);
  }, [feedbacks]);

  const fetchFeedbackSystemStatus = async () => {
    try {
      const response = await axios.post('http://localhost:8089/api/admin/feedback/enable-feedback');
      setIsFeedbackEnabled(response.data.isEnabled);
    } catch (error) {
      console.error("Error fetching feedback system status", error);
    }
  };

  const fetchTriggers = async () => {
    try {
      const response = await axios.post('http://localhost:8089/api/admin/feedback/view-triggers');
      setTriggers(response.data);
    } catch (error) {
      console.error("Error fetching triggers", error);
    }
  };

  const fetchFeedbacks = async () => {
    try {
      const response = await axios.post('http://localhost:8089/api/admin/feedback/view-all');
      setFeedbacks(response.data);
    } catch (error) {
      console.error("Error fetching feedbacks", error);
    }
  };

  const handleToggleFeedbackSubmission = async (enabled) => {
    try {
      await axios.post('http://localhost:8089/api/admin/feedback/enable-feedback', { isEnabled: enabled });
      setIsFeedbackEnabled(enabled);
      notification.success({
        message: 'Feedback System Status Updated',
        description: `Feedback submission is now ${enabled ? 'enabled' : 'disabled'}.`,
      });
    } catch (error) {
      notification.error({
        message: 'Error updating feedback system status',
        description: 'Please try again later.',
      });
    }
  };

  const handleAddTrigger = async (values) => {
    try {
      await axios.post('http://localhost:8089/api/admin/feedback/set-trigger', values);
      notification.success({
        message: 'Feedback Trigger Set Successfully',
      });
      fetchTriggers();
    } catch (error) {
      notification.error({
        message: 'Error setting feedback trigger',
        description: 'Please try again later.',
      });
    }
  };

  const handleDeleteFeedback = async (feedbackId) => {
    try {
      await axios.post(`http://localhost:8089/api/user/feedback/delete/${feedbackId}`);
      notification.success({
        message: 'Feedback Deleted',
        description: 'Feedback has been deleted successfully.',
      });
      fetchFeedbacks();
    } catch (error) {
      notification.error({
        message: 'Error deleting feedback',
        description: 'Please try again later.',
      });
    }
  };

  const handleSearch = (value) => {
    setSearchText(value);
    const filtered = feedbacks.filter(fb =>
      fb.feedbackText.toLowerCase().includes(value.toLowerCase()) ||
      fb.comments.toLowerCase().includes(value.toLowerCase()) ||
      fb.rating.toString().includes(value)
    );
    setFilteredFeedbacks(filtered);
  };

  const handleDateFilter = (value) => {
    let filtered = feedbacks;
    const today = moment().startOf('day');

    if (value === "today") {
      filtered = feedbacks.filter(fb => moment(fb.createdAt).isSame(today, 'day'));
    } else if (value === "week") {
      filtered = feedbacks.filter(fb => moment(fb.createdAt).isAfter(moment().subtract(7, 'days')));
    } else if (value === "month") {
      filtered = feedbacks.filter(fb => moment(fb.createdAt).isAfter(moment().subtract(1, 'month')));
    }

    setFilteredFeedbacks(filtered);
    setDateFilter(value);
  };

  const filteredFeedbacksData = filteredFeedbacks.filter((feedback) =>
    Object.values(feedback).some(
      (val) =>
        val &&
        val.toString().toLowerCase().includes(searchText.toLowerCase())
    )
  );

  const feedbackColumns = [
    { title: 'User ID', dataIndex: 'userId', key: 'userId' },
    { title: 'Feedback Text', dataIndex: 'feedbackText', key: 'feedbackText' },
    { title: 'Rating', dataIndex: 'rating', key: 'rating' },
    { title: 'Comments', dataIndex: 'comments', key: 'comments' },
    { title: 'Anonymous', dataIndex: 'isAnonymous', key: 'isAnonymous', render: (val) => (val ? 'Yes' : 'No') },
    {
      title: 'Created At',
      dataIndex: 'createdAt',
      key: 'createdAt',
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: 'Updated At',
      dataIndex: 'updatedAt',
      key: 'updatedAt',
      render: (date) => moment(date).format('YYYY-MM-DD HH:mm:ss'),
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Button type="primary" danger onClick={() => handleDeleteFeedback(record.id)}>
          Delete
        </Button>
      ),
    },
  ];

  return (
    <div style={{ padding: 20 }}>
      <div style={{ marginBottom: 20 }}>
        <Switch
          checked={isFeedbackEnabled}
          onChange={handleToggleFeedbackSubmission}
          checkedChildren="Enabled"
          unCheckedChildren="Disabled"
        />
        <span style={{ marginLeft: 10 }}>Enable Feedback Submission</span>
      </div>

      <Card
        title="Add Feedback Trigger"
        hoverable
        style={{ marginBottom: 20, borderRadius: 10 }}
      >
        <Form layout="vertical" onFinish={handleAddTrigger}>
          <Form.Item label="Activity Type" name="activityType">
            <Input placeholder="e.g., Cancel" />
          </Form.Item>
          <Form.Item label="Trigger Count" name="triggerCount">
            <Input type="number" />
          </Form.Item>
          <Form.Item label="Feedback Mandatory" name="isFeedbackMandatory" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item label="Rating Required" name="isRatingRequired" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item label="Comments Required" name="isCommentsRequired" valuePropName="checked">
            <Switch />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit">Add Trigger</Button>
          </Form.Item>
        </Form>
      </Card>

      {/* Search Bar and Date Filter */}
      <div style={{ marginBottom: 20 }}>
        <Input.Search
          placeholder="Search feedbacks..."
          onSearch={handleSearch}
          allowClear
          style={{ width: 300, marginRight: 10 }}
        />
        <Select
          defaultValue=""
          onChange={handleDateFilter}
          style={{ width: 200 }}
        >
          <Option value="">All Time</Option>
          <Option value="today">Today</Option>
          <Option value="week">Past Week</Option>
          <Option value="month">Past Month</Option>
        </Select>
      </div>

      {/* Feedback Table */}
      <h3 style={{ color: '#f5222d' }}>All Feedbacks</h3>
      <Table
        dataSource={filteredFeedbacksData}
        columns={feedbackColumns}
        pagination={{ pageSize: 5 }}
      />

      {/* Trigger Table */}
      <h3 style={{ color: '#52c41a' }}>All Trigger Activity Types</h3>
      <Table
        dataSource={triggers}
        columns={[
          { title: 'Activity Type', dataIndex: 'activityType', key: 'activityType' },
          { title: 'Trigger Count', dataIndex: 'triggerCount', key: 'triggerCount' },
          { title: 'Feedback Mandatory', dataIndex: 'isFeedbackMandatory', key: 'isFeedbackMandatory', render: (value) => (value ? 'Required' : 'Not Required') },
          { title: 'Rating Required', dataIndex: 'isRatingRequired', key: 'isRatingRequired', render: (value) => (value ? 'Required' : 'Not Required') },
          { title: 'Comments Required', dataIndex: 'isCommentsRequired', key: 'isCommentsRequired', render: (value) => (value ? 'Required' : 'Not Required') },
        ]}
      />
    </div>
  );
};

export default FeedbackAdminPage;
