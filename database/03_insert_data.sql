INSERT INTO `drone` (`serial_number`, `Drone_Model_id`, `battery_capacity`, `status`)
VALUES
  ('DRO001', 1, 80, 'IDLE'),
  ('DRO002', 2, 90, 'IDLE'),
  ('DRO003', 3, 70, 'IDLE'),
  ('DRO004', 4, 85, 'IDLE'),
  ('DRO005', 1, 75, 'IDLE'),
  ('DRO006', 2, 95, 'IDLE'),
  ('DRO007', 3, 60, 'IDLE'),
  ('DRO008', 4, 88, 'IDLE'),
  ('DRO009', 1, 78, 'IDLE'),
  ('DRO010', 2, 92, 'IDLE');

-- Insert data into medication table
INSERT INTO `medication` (`code`, `name`, `weight`, `image_url`)
VALUES
  ('MED001', 'Aspirin', 100, 'https://example.com/aspirin_image.jpg'),
  ('MED002', 'Ibuprofen', 200, 'https://example.com/ibuprofen_image.jpg'),
  ('MED003', 'Paracetamol', 150, 'https://example.com/paracetamol_image.jpg'),
  ('MED004', 'Antibiotic', 300, 'https://example.com/antibiotic_image.jpg'),
  ('MED005', 'Cough_Syrup', 250, 'https://example.com/cough_syrup_image.jpg'),
  ('MED006', 'Band_Aid', 50, 'https://example.com/band_aid_image.jpg'),
  ('MED007', 'Thermometer', 100, 'https://example.com/thermometer_image.jpg'),
  ('MED008', 'Vitamin_C', 150, 'https://example.com/vitamin_c_image.jpg'),
  ('MED009', 'Painkiller', 200, 'https://example.com/painkiller_image.jpg'),
  ('MED010', 'Antacid', 250, 'https://example.com/antacid_image.jpg');
