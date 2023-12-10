import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:frontend_mobile/models/catalog.dart';
import 'dart:typed_data';

class CatalogService {
  final String baseUrl;

  CatalogService({required this.baseUrl});

  Future<List<Result>> getAllCatalogs() async {
    final response = await http.get(Uri.parse('$baseUrl/api/catalog/all'));

    if (response.statusCode == 200) {
      final List<dynamic> data = json.decode(response.body)['result'];
      return data.map((model) => Result.fromJson(model)).toList();
    } else {
      throw Exception('Failed to load catalogs');
    }
  }

  Future<Uint8List> getImage(String catalogId) async {
    final response =
        await http.get(Uri.parse('$baseUrl/api/catalog/image/$catalogId'));

    if (response.statusCode == 200) {
      return Uint8List.fromList(response.bodyBytes);
    } else {
      throw Exception('Failed to load image');
    }
  }

  Future<Result> getCatalogById(String catalogId) async {
    final response =
        await http.get(Uri.parse('$baseUrl/api/catalog/$catalogId'));

    if (response.statusCode == 200) {
      final Map<String, dynamic> data = json.decode(response.body)['result'];
      return Result.fromJson(data);
    } else {
      throw Exception('Failed to load catalog by ID');
    }
  }

  Future<List<Result>> getFilteredProducts({
    String category = 'all',
    int? startPrice,
    int? endPrice,
  }) async {
    final headers = {'Content-Type': 'application/json'};
    String encodeCategory =
        category.replaceAll(" ", "-").replaceAll("&", "n").toLowerCase();
    final catalogUrl = '$baseUrl/api/catalog/filter';

    Uri uri = Uri.parse(catalogUrl);

    if (startPrice != null && endPrice != null) {
      uri = Uri.parse('$catalogUrl?startPrice=$startPrice&endPrice=$endPrice');
    } else if (category != 'all') {
      uri = Uri.parse('$catalogUrl?categoryName=$encodeCategory');
    }

    try {
      final response = await http.get(
        uri,
        headers: headers,
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        final Allproducts allProducts = Allproducts.fromJson(data);
        return allProducts.result;
      } else {
        throw Exception("Failed to load products");
      }
    } catch (e) {
      throw Exception("An error occurred: $e");
    }
  }

  Future<List<CategoryId>> getAllCategories() async {
    final headers = {'Content-Type': 'application/json'};
    final response = await http.get(Uri.parse('$baseUrl/api/category/all'),
        headers: headers);

    try {
      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        final Allproducts allProducts = Allproducts.fromJson(data);
        return allProducts.result.map((result) => result.categoryId).toList();
      } else {
        throw Exception("Failed to load categories");
      }
    } catch (e) {
      throw Exception("An error occurred: $e");
    }
  }
}
