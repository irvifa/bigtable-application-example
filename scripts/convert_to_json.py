import argparse
import csv
import json

def convert_csv_to_json(input_file, output_file):
    data = []
    with open(input_file) as f:
        csv_reader = csv.DictReader(f)
        for row in csv_reader:
            datum = dict(
            dateStr=row['Date'],
            latitude=float(row['Latitude']),
            longitude=float(row['Longitude']),
            regionName=row['RegionName'],
            hospitalizedPatients=int(row['HospitalizedPatients']),
            icuPatients=int(row['IntensiveCarePatients']),
            totalHospitalizadPatients=int(row['TotalHospitalizedPatients']),
            homeConfinement=int(row['HomeConfinement']),
            currentPositiveCases=int(row['CurrentPositiveCases']),
            newPositiveCases=int(row['NewPositiveCases']),
            recovered=int(row['Recovered']),
            deaths=int(row['Deaths']),
            totalPositiveCases=int(row['TotalPositiveCases'])
            )
            data.append(datum)

    summary = dict(summary=data)
    with open(output_file, 'w+') as f:
        f.write(json.dumps(summary, indent=4))

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--input")
    parser.add_argument("--output")
    result = parser.parse_args()
    convert_csv_to_json(result.input, result.output)
