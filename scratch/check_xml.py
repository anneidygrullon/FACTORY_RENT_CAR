import xml.etree.ElementTree as ET
import sys

try:
    ET.parse('src/main/resources/com/example/factory_rent_car/MainLayout.fxml')
    print("XML is valid")
except ET.ParseError as e:
    print(f"XML Parse Error: {e}")
except Exception as e:
    print(f"Error: {e}")
