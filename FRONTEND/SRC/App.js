import React, { useEffect, useState } from 'react';
import { Layout, Menu, Card, Row, Col, Typography, Table } from 'antd';
import { Pie, Bar, Line } from 'react-chartjs-2';
import axios from 'axios';
import FeedbackAdminPage from './components/FeedbackAdminPage'; // Import the Feedback Admin Page component
import UserPage from './components/UserPage'; // Import UserPage component
import {
  Chart as ChartJS,
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  Title as ChartTitle,
  Tooltip,
  Legend
} from 'chart.js';

const { Header, Content, Sider } = Layout;
const { Title: AntTitle, Text } = Typography;

ChartJS.register(
  ArcElement,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ChartTitle,
  Tooltip,
  Legend
);

const App = () => {
  const [analyticsData, setAnalyticsData] = useState(null);
  const [selectedMenuKey, setSelectedMenuKey] = useState('1'); // State to handle menu selection

  useEffect(() => {
    if (selectedMenuKey === '2') {
      fetchFeedbackAnalytics(); // Fetch analytics only when the analytics menu is selected
    }
  }, [selectedMenuKey]);

  const fetchFeedbackAnalytics = async () => {
    try {
      const response = await axios.post('http://localhost:8089/api/admin/feedback/analytics-report');
      setAnalyticsData(response.data);
    } catch (error) {
      console.error("Error fetching analytics data", error);
    }
  };

  if (!analyticsData && selectedMenuKey === '2') {
    return <div>Loading...</div>;
  }

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider width={200} className="site-layout-background">
        <Menu
          mode="inline"
          defaultSelectedKeys={['1']}
          style={{ height: '100%', borderRight: 0 }}
          onSelect={({ key }) => setSelectedMenuKey(key)} // Handle menu selection
        >
          <Menu.Item key="1">Feedback Management</Menu.Item>
          <Menu.Item key="2">Analytics</Menu.Item>
          <Menu.Item key="3">User Page</Menu.Item> {/* Added UserPage menu item */}
        </Menu>
      </Sider>
      <Layout style={{ padding: '0 24px 24px' }}>
        <Header style={{ padding: 0 }}>
          <AntTitle>Admin Dashboard</AntTitle>
        </Header>
        <Content
          style={{
            padding: 24,
            margin: 0,
            minHeight: 280,
          }}
        >
          {/* Conditional rendering based on selected menu */}
          {selectedMenuKey === '1' && <FeedbackAdminPage />} 
          {selectedMenuKey === '2' && (
            <>
              <Row gutter={16}>
                <Col span={8}>
                  <Card title="Low Rating Percentage" style={{ width: 300 }}>
                    <Text style={{ color: 'red' }}>{`${analyticsData.lowRatingPercentage}%`}</Text>
                  </Card>
                </Col>
                <Col span={8}>
                  <Card title="Average Rating" style={{ width: 300 }}>
                    <Text>{analyticsData.averageRating}</Text>
                  </Card>
                </Col>
                <Col span={8}>
                  <Card title="Feedback Trends" style={{ width: 300 }}>
                    <Text>{Object.keys(analyticsData.feedbackTrends).map(key => `${key}: ${analyticsData.feedbackTrends[key]}`).join(', ')}</Text>
                  </Card>
                </Col>
              </Row>

              <Row gutter={16}>
                <Col span={12}>
                  <Card title="Ratings Over Time" style={{ width: '100%' }}>
                    {analyticsData.ratingsOverTime && Object.keys(analyticsData.ratingsOverTime).length > 0 ? (
                      <Line
                        data={{
                          labels: Object.keys(analyticsData.ratingsOverTime),
                          datasets: [{
                            data: Object.values(analyticsData.ratingsOverTime),
                            borderColor: '#FF5733',
                            backgroundColor: 'rgba(255, 87, 51, 0.2)',
                          }]
                        }}
                        options={{ responsive: true }}
                      />
                    ) : (
                      <div>No data available</div>
                    )}
                  </Card>
                </Col>
                <Col span={12}>
                  <Card title="Feedback Distribution" style={{ width: '100%' }}>
                    <Table
                      columns={[
                        { title: 'Rating', dataIndex: 'rating', key: 'rating' },
                        { title: 'Count', dataIndex: 'count', key: 'count' }
                      ]}
                      dataSource={Object.entries(analyticsData.ratingDistribution).map(([rating, count]) => ({
                        key: rating,
                        rating,
                        count,
                      }))}
                      pagination={false}
                    />
                  </Card>
                </Col>
              </Row>
            </>
          )}
          {selectedMenuKey === '3' && <UserPage />} {/* Render UserPage when selected */}
        </Content>
      </Layout>
    </Layout>
  );
};

export default App;
