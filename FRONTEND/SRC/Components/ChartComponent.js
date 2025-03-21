import { Chart } from 'chart.js';
import { useEffect, useRef } from 'react';

const ChartComponent = ({ data }) => {
  const chartRef = useRef(null);
  
  useEffect(() => {
    if (chartRef.current?.chart) {
      chartRef.current.chart.destroy();
    }

    const chartInstance = new Chart(chartRef.current, {
      type: 'pie',  
      data: data,
      options: {
        responsive: true,
      }
    });

    chartRef.current.chart = chartInstance;

    return () => {
      chartInstance.destroy();
    };
  }, [data]);

  return <canvas ref={chartRef} />;
};

export default ChartComponent;
