import { Button, Card, Form, Input, Modal, notification, Rate, Switch, Table } from 'antd';
import axios from 'axios';
import moment from 'moment';
import React, { useEffect, useState } from 'react';

const { TextArea } = Input;

const UserPage = () => {
  const [products, setProducts] = useState([]);
  const [feedbacks, setFeedbacks] = useState([]);
  const [triggeredFeedback, setTriggeredFeedback] = useState(false);
  const [showFeedbackForm, setShowFeedbackForm] = useState(false);
  const [selectedFeedback, setSelectedFeedback] = useState(null);
  const [feedbackText, setFeedbackText] = useState('');
  const [rating, setRating] = useState(0);
  const [isAnonymous, setIsAnonymous] = useState(true);
  const [comments, setComments] = useState('');
  const [searchText, setSearchText] = useState(''); // For search functionality

  const userId = 1; // Replace with actual user ID

  useEffect(() => {
    fetchProducts();
    fetchFeedbacks();
  }, []);

  const fetchProducts = async () => {
    const productList = [
      { id: 1, name: 'Product 1', image: '/download (1).jpeg' },
      { id: 2, name: 'Product 2', image: '/download.jpeg' },
      { id: 3, name: 'Product 3', image: '/images.jpeg' },
    ];
    setProducts(productList);
  };

  const fetchFeedbacks = async () => {
    try {
      const response = await axios.get(`http://localhost:8089/api/user/feedback/view/${userId}`);
      setFeedbacks(response.data);
    } catch (error) {
      console.error('Error fetching feedbacks', error);
    }
  };

  const trackActivity = async (activityType) => {
    try {
      const response = await axios.post('http://localhost:8089/api/user/feedback/track', {
        userId,
        activityType,
      });

      if (response.data.triggerFeedback) {
        setTriggeredFeedback(true);
        setShowFeedbackForm(true);
      }
    } catch (error) {
      console.error('Error tracking activity', error);
    }
  };

  const submitFeedback = async () => {
    try {
      const response = await axios.post('http://localhost:8089/api/user/feedback/submit', {
        userId,
        feedbackText,
        rating,
        isAnonymous,
        comments,
      });
      notification.success({ message: 'Feedback submitted successfully!' });
      setShowFeedbackForm(false);
      fetchFeedbacks(); 
    } catch (error) {
      notification.error({ message: 'Error submitting feedback Because Feedback is Disabled' });
    }
  };

  const deleteFeedback = async (feedbackId) => {
    try {
      await axios.post(`http://localhost:8089/api/user/feedback/delete/${feedbackId}`);
      notification.success({ message: 'Feedback deleted successfully!' });
      fetchFeedbacks(); 
    } catch (error) {
      notification.error({ message: 'Error deleting feedback' });
    }
  };

  // const updateFeedback = async () => {
  //   try {
  //     await axios.post(`http://localhost:8089/api/user/feedback/update/${selectedFeedback.id}`, {
  //       feedbackText,
  //       rating,
  //       comments,
  //     });
  //     notification.success({ message: 'Feedback updated successfully!' });
  //     fetchFeedbacks(); 
  //   } catch (error) {
  //     notification.error({ message: 'Error updating feedback' });
  //   }
  // };
  const updateFeedback = async () => {
    try {
      const response = await axios.put(`http://localhost:8089/api/user/feedback/update/${selectedFeedback.id}`, {
        feedbackId: selectedFeedback.id,  // Ensure feedbackId is included
        feedbackText,  // Feedback text
        rating,        // Rating
        comments,      // Comments
      });
      notification.success({ message: 'Feedback updated successfully!' });
      fetchFeedbacks();
    } catch (error) {
      console.error('Error updating feedback:', error);  // Log full error
      notification.error({ message: 'Error updating feedback' });
    }
  };
  
  const handleSearch = (e) => {
    setSearchText(e.target.value);
  };

  const productColumns = [
    { title: 'Product', dataIndex: 'name', key: 'name' },
    {
      title: 'Image',
      dataIndex: 'image',
      key: 'image',
      render: (text) => <img src={text} alt="Product" width={150} style={{ borderRadius: '8px' }} />,
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <>
          <Button type="primary" onClick={() => trackActivity('purchase product')}>
            Purchase Product
          </Button>
          <Button type="danger" onClick={() => trackActivity('Cancel order')} style={{ marginLeft: 10 }}>
            Cancel Order
          </Button>
        </>
      ),
    },
  ];

  const feedbackColumns = [
    { title: 'Feedback Text', dataIndex: 'feedbackText', key: 'feedbackText' },
    { title: 'Rating', dataIndex: 'rating', key: 'rating' },
    { title: 'Anonymous', dataIndex: 'isAnonymous', key: 'isAnonymous', render: (value) => (value ? 'Yes' : 'No') },
    { title: 'Comments', dataIndex: 'comments', key: 'comments' },
    { title: 'Created At', dataIndex: 'createdAt', key: 'createdAt', render: (value) => moment(value).format('YYYY-MM-DD HH:mm:ss') },
    { title: 'Updated At', dataIndex: 'updatedAt', key: 'updatedAt', render: (value) => moment(value).format('YYYY-MM-DD HH:mm:ss') },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => {
        const isUpdatable = moment().diff(moment(record.createdAt), 'minutes') <= 15; // Check if within 15 minutes
        return (
          <>
            <Button
              type="link"
              onClick={() => { setSelectedFeedback(record); setFeedbackText(record.feedbackText); setRating(record.rating); setComments(record.comments || ''); setShowFeedbackForm(true); }}
              disabled={!isUpdatable}
            >
              Update
            </Button>
            <Button
              type="link"
              danger
              onClick={() => deleteFeedback(record.id)}
              disabled={!isUpdatable}
            >
              Delete
            </Button>
          </>
        );
      },
    },
  ];

  // Filter feedbacks based on search text
  const filteredFeedbacks = feedbacks.filter(feedback => 
    feedback.feedbackText.toLowerCase().includes(searchText.toLowerCase()) ||
    feedback.comments.toLowerCase().includes(searchText.toLowerCase()) ||
    feedback.createdAt.toLowerCase().includes(searchText.toLowerCase()) ||
    feedback.updatedAt.toLowerCase().includes(searchText.toLowerCase())
  );

  return (
    <div style={{ padding: 20 }}>
      <h3>Products</h3>
      <div style={{ display: 'flex', flexWrap: 'wrap' }}>
        {products.map(product => (
          <Card
            key={product.id}
            hoverable
            style={{ width: 240, margin: '10px' }}
            cover={<img alt={product.name} src={product.image} style={{ height: 150, objectFit: 'cover' }} />}
          >
            <Card.Meta title={product.name} />
            <Button type="primary" style={{ marginTop: 10 }} onClick={() => trackActivity('purchase product')}>
              Purchase
            </Button>
            <Button type="danger" style={{ marginTop: 10, marginLeft: 10 }} onClick={() => trackActivity('Cancel order')}>
              Cancel Order
            </Button>
          </Card>
        ))}
      </div>

      <h3>Your Feedbacks</h3>

      {/* Search Bar */}
      <Input
        style={{ marginBottom: '10px', width: '100%' }}
        placeholder="Search Feedback"
        value={searchText}
        onChange={handleSearch}
      />

      <Table dataSource={filteredFeedbacks} columns={feedbackColumns} rowKey="id" />

      <Modal
        title={selectedFeedback ? 'Update Feedback' : 'Submit Feedback'}
        visible={showFeedbackForm}
        onCancel={() => setShowFeedbackForm(false)}
        onOk={selectedFeedback ? updateFeedback : submitFeedback}
      >
        <Form layout="vertical">
          <Form.Item label="Feedback Text">
            <Input.TextArea value={feedbackText} onChange={(e) => setFeedbackText(e.target.value)} />
          </Form.Item>
          <Form.Item label="Rating">
            <Rate value={rating} onChange={setRating} />
          </Form.Item>
          <Form.Item label="Anonymous">
            <Switch checked={isAnonymous} onChange={(checked) => setIsAnonymous(checked)} />
          </Form.Item>
          <Form.Item label="Comments">
            <TextArea value={comments} onChange={(e) => setComments(e.target.value)} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};

export default UserPage;
