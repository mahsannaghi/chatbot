package com.paya.EncouragementService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithSimpleName;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import com.nimbusds.jose.shaded.gson.Gson;
import com.paya.EncouragementService.dto.humanResource.HumanResourceRelatedManagerDTO;
import com.paya.EncouragementService.enumeration.EncouragementResultEnum;
import com.paya.EncouragementService.enumeration.ReviewResultEnum;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@OpenAPIDefinition(info = @Info(title = "Encouragement Service API", version = "v1"))
@SpringBootApplication
@EnableFeignClients
public class EncouragementServiceApplication {
	static List<String> methodsFromFile;
	static List<MethodDeclaration> methodsList= new ArrayList<>();
	static List<HashMap<String, Set<String>>> methodsInnerMethodList= new ArrayList<>();
	static Map<String, Set<String>> businessBlocksMapTag = new HashMap<>();
	static Map<String, String> businessBlocksMap;
	static Map<String, List<Map<String, Object>>> blocksMap;
	static List<String> methodsNameFromFile= new ArrayList<>();
	public static void main(String[] args) throws Exception {
		final Map env = Dotenv.load().entries().stream().collect(Collectors.toMap(DotenvEntry::getKey, DotenvEntry::getValue));
		(new SpringApplicationBuilder(EncouragementServiceApplication.class)).environment(new StandardEnvironment() {
			protected void customizePropertySources(MutablePropertySources propertySources) {
				super.customizePropertySources(propertySources);
				propertySources.addFirst(new MapPropertySource("dotenvProperties", env));
			}
		}).run(args);
		System.out.println("Current working dir: " + new File(".").getAbsolutePath());

		SourceRoot sourceRoot = new SourceRoot(Paths.get("F:\\Projects\\encouragement-develop\\encouragement-service\\src\\main\\java\\com\\paya\\EncouragementService\\service"));
		Map<String, CompilationUnit> allClasses = new HashMap<>();
		Set<String> wantedClasses = Set.of("EncouragementService", "EncouragementReviewService");

		sourceRoot.tryToParse().forEach(result -> result.ifSuccessful(cu -> {
			cu.getPrimaryTypeName().ifPresent(name -> {
				if (wantedClasses.contains(name)) {
					allClasses.put(name, cu); // ✅ ذخیره خود CompilationUnit نه Optional
				}
			});
		}));
//
		XWPFDocument document = new XWPFDocument();

		Map<String, String> classToRepoField = new HashMap<>();
		List<MethodDeclaration> methodsUsedRepository = new ArrayList<>();
		List<MethodDeclaration> savingList= new ArrayList<>();
		Map<String, List<MethodDeclaration>> savingMap = new HashMap<>();
		Set<String> methodsCallingInnerMethodSet = new HashSet<>();
		Map<String, Set<String>> methodsCallingInnerMethodMap = new HashMap<>();

		for (Map.Entry<String, CompilationUnit> entry : allClasses.entrySet()) {
			String className = entry.getKey();
			CompilationUnit cu = entry.getValue();

//			System.out.println("Class: " + className);

			cu.getTypes().forEach(type -> {
				if (type.isClassOrInterfaceDeclaration()) {
					ClassOrInterfaceDeclaration cls = type.asClassOrInterfaceDeclaration();
					methodsList.addAll(cls.getMethods());
				}
			});

			cu.accept(new VoidVisitorAdapter<Void>() {
				@Override
				public void visit(FieldDeclaration n, Void arg) {
					super.visit(n, arg);

					n.getVariables().forEach(variableDeclarator -> {
						String fieldType = n.getElementType().toString();
						if (fieldType.equals("EncouragementReviewRepository")) {
							Optional<ClassOrInterfaceDeclaration> parentClass = n.findAncestor(ClassOrInterfaceDeclaration.class);
							parentClass.ifPresent(classOrInterfaceDeclaration -> classToRepoField.put(classOrInterfaceDeclaration.getNameAsString(), variableDeclarator.getNameAsString()));
						}
					});
				}
			}, null);
		}

		for (Map.Entry<String, CompilationUnit> entry : allClasses.entrySet()) {
			CompilationUnit cu = entry.getValue();
			cu.accept(new VoidVisitorAdapter<Void>() {
				@Override
				public void visit(MethodCallExpr mc, Void arg) {
					super.visit(mc, arg);

					mc.getScope().ifPresent(scope -> {
						String scopeName = scope.toString();

						for (String repoName : classToRepoField.values()) {

							if (scopeName.equals(repoName)) {

								Optional<MethodDeclaration> parentMethod = mc.findAncestor(MethodDeclaration.class);

								parentMethod.ifPresent(methodsUsedRepository::add);
							}
						}
					});
				}
			}, null);
		}

		for (MethodDeclaration method : methodsUsedRepository) {
			method.accept(new VoidVisitorAdapter<Void>() {
				@Override
				public void visit(MethodCallExpr mc, Void arg) {
					super.visit(mc, arg);

					String methodName = mc.getNameAsString();

					if (methodName.equals("save") || methodName.equals("saveOrUpdate")) {

						mc.getScope().ifPresent(scope -> {

							String scopeName = scope.toString();

							// پیدا کردن کلاس والد متد call
							Optional<ClassOrInterfaceDeclaration> parentClassOpt = mc.findAncestor(ClassOrInterfaceDeclaration.class);
							Optional<MethodDeclaration> methodDeclaration = mc.findAncestor(MethodDeclaration.class);

							methodDeclaration.ifPresent(savingList::add);
						});
					}
				}
			}, null);
			savingMap.put("EncouragementReviewSaving", savingList);
		}

		Map<String, File> allFiles = new HashMap<>();
		allFiles.put("EncouragementReviewService", new File("F://Projects//encouragement-develop//encouragement-service//src//main//java//com//paya//EncouragementService//service//EncouragementReviewService.java"));
		allFiles.put("EncouragementReview", new File("F://Projects//encouragement-develop//encouragement-service//src//main//java//com//paya//EncouragementService//entity//EncouragementReview.java"));
		allFiles.put("EncouragementService", new File("F://Projects//encouragement-develop//encouragement-service//src//main//java//com//paya//EncouragementService//service//EncouragementService.java"));

		CombinedTypeSolver typeSolver = new CombinedTypeSolver();
		typeSolver.add(new ReflectionTypeSolver());
		typeSolver.add(new JavaParserTypeSolver(new File("F://Projects//encouragement-develop//encouragement-service//src//main//java")));
		JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
		ParserConfiguration config = new ParserConfiguration().setSymbolResolver(symbolSolver);
		StaticJavaParser.setConfiguration(config);

// تست
		ResolvedReferenceTypeDeclaration resolved = typeSolver.solveType(
				"com.paya.EncouragementService.service.EncouragementReviewService"
		);
		System.out.println("Class found: " + resolved.getQualifiedName());

		for (Map.Entry<String, File> entry : allFiles.entrySet()) {
			StaticJavaParser.setConfiguration(config);
			CompilationUnit cu = StaticJavaParser.parse(entry.getValue());
			cu.accept(new VoidVisitorAdapter<Void>() {
				@Override
				public void visit(MethodDeclaration mc, Void arg) {
					super.visit(mc, arg);

					mc.accept(new VoidVisitorAdapter<Void>() {
						@Override
						public void visit(MethodCallExpr call, Void arg) {
							super.visit(call, arg);
							for (Map.Entry<String, List<MethodDeclaration>> savedMethodMap : savingMap.entrySet()) {
								List<MethodDeclaration> savedMethodValue = savedMethodMap.getValue();
								for (MethodDeclaration savedMethod : savedMethodValue) {
									if (!savedMethod.getNameAsString().equals(call.getNameAsString())) continue;
									if (savedMethod.getParameters().size() != call.getArguments().size()) continue;
									List<String> argTypes = new ArrayList<>();
									try {
										for (Expression arg1 : call.getArguments()) {
											String typeName = arg1.calculateResolvedType().describe(); // نیاز به SymbolSolver
											// فقط نام ساده نوع (بدون package)
											typeName = typeName.substring(typeName.lastIndexOf(".") + 1);
											argTypes.add(typeName);
										}
										if (!(savedMethod.getParameters().stream().map(parameter -> parameter.getType().toString()).toList().equals(argTypes)))
											continue;
										methodsCallingInnerMethodSet.add(mc.getNameAsString());
									} catch (Exception exception) {

									}
								}
							}
						}
					}, null);
				}
			}, null);
			methodsCallingInnerMethodMap.put("EncouragementReviewSaving", methodsCallingInnerMethodSet);
		}

//		for (MethodDeclaration method : methodsCallingInnerMethod) {
//			method.accept(new VoidVisitorAdapter<Void>() {
//				@Override
//				public void visit(MethodCallExpr mc, Void arg) {
//					super.visit(mc, arg);
//
//					String methodName = mc.getNameAsString();
//
//					mc.getScope().ifPresent(scope -> {
//
//						String scopeName = scope.toString();
//						Optional<MethodCallExpr> methodDeclaration = mc.findAncestor(MethodCallExpr.class);
//
//						methodDeclaration.ifPresent(allInnerAndOuterMethods::add);
//					});
//				}
//			}, null);
//		}

		blocksMap = extractIfElseLinesMap(methodsList);
//		Map<String, String> businessBlocksMap = new HashMap<>();
//		Map<String, String> methodFlowChartMap = new HashMap<>();
//		Map<String, List<Double>> blocksEmbeddings = getBlocksEmbeddings(blocksMap);


		String json = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/javaBlock.json"));
//		Type type = new TypeToken<LinkedHashMap<String, List<String>>>(){}.getType();
//		Map<String, List<String>> embeddingsMap = new Gson().fromJson(json, type);
//		Map<String, List<String>> updatedMap = new LinkedHashMap<>();
//
//		for (Map.Entry<String, List<String>> entry : embeddingsMap.entrySet()) {
//			String key = entry.getKey();
//			List<String> oldBlocks = entry.getValue();
//			List<String> newBlocks = new ArrayList<>();
//
//			for (String block : oldBlocks) {
//				// فراخوانی تابع برای هر بلاک
//				String explanation = getBusiness(block);
//				newBlocks.add(explanation);
//			}
//
//			// کلید همان کلید قبلی، مقدار جدید
//			updatedMap.put(key, newBlocks);
//		}


//		Iterator<Map.Entry<String, String>> iterator = blocksMap.entrySet().iterator();
////
//		while (iterator.hasNext()) {
//			Map.Entry<String, String> next = iterator.next();
//			String explanation = null;
////			explanation = getIfBusiness(next.getKey());
//			businessBlocksMap.put(next.getKey(), next.getValue());
//			System.out.println(businessBlocksMap);
//			System.out.println("end");
//			writeDoc(document, businessBlocksMap);
//
//			Optional<String> innerLine = Arrays.stream(explanation.split("\\R"))
//					.map(String::trim)
//					.filter(line -> line.startsWith("InnerMethods: "))
//					.findFirst();
//
//			List<String> innerMethods = innerLine
//					.map(line -> Arrays.stream(line.split("[\",:\\s]+"))
//							.filter(s -> !s.isEmpty() && !s.equals("InnerMethods"))
//							.toList())
//					.orElse(List.of());
//
//			// ✅ inner methods را تحلیل کن
//			innerMethods.forEach(s -> {
//				String method = getBusinessWithInnerMethod(s);
//				writeDoc(document, method);
//			});
//
//			// ✅ inner methods را از همان لیست حذف کن (در جا)
//			methodsList.removeIf(md -> innerMethods.contains(md.getName().toString()));
//		}

//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String outputFilePath = "F:/Projects/encouragement-develop/encouragement-service/blockMapWithTotalCodeWithNegativeElse.json";
//		try (FileWriter writer = new FileWriter(outputFilePath, StandardCharsets.UTF_8)) {
//			gson.toJson(businessBlocksMap, writer);
//			System.out.println("Updated map saved to " + outputFilePath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}


//		while (iterator.hasNext()) {
//			MethodDeclaration methodDeclaration = iterator.next();
//			String methodName = methodDeclaration.getNameAsString();   // اسم متد
//			String code = methodDeclaration.toString();                // کد متد
//			List<Double> embedding = getEmbedding(code);              // گرفتن embedding
//			embeddingsMap.put(methodName, embedding);                 // اضافه کردن به Map
//		}
//
//

//		Iterator<MethodDeclaration> iterator = methodsList.iterator();
//		while (iterator.hasNext()) {
//			MethodDeclaration next = iterator.next();
//			List<String> methods = new ArrayList();
//			methods.add("updateEachEncouragementReviewThatNeeded");
//			methods.add("updateEncouragementReview");
//			methods.add("forwardNextManager");
//			methods.add("sentForRegistrar");
//			methods.add("deleteRegistrarReviewIfExist");
//			methods.add("forwardPreviousManager");
//			methods.add("commissionInput");
//			methods.add("afterCheckingRegistrarPowerLimit");
//			methods.add("calculator");
//			methods.add("addOrUpdateEncouragement");
//			if (methods.contains(next.getNameAsString())) {
//				explanation = getBusinessWithInnerMethod(next.toString());
//				methodFlowChartMap.put(next.getName().toString(), explanation);
//			}
//		}


		String code= "" +
				"    private void forwardNextManager(Encouragement encouragement, PersonnelDTO personnelReviewCreator, PersonnelManagerDTO personnelManager, List<String> managerPositionList, int creatorIndex, Optional<EncouragementReview> thisEncouragementReview, boolean deleteRecentManagerReview, PersonnelDTO encouragedPerson) {\n" +
				"        if (creatorIndex + 1 < managerPositionList.size()) {\n" +
				"            String nextManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex + 1);\n" +
				"            Optional<EncouragementReview> nextEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(nextManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());\n" +
				"            if (creatorIndex - 1 >= 0) {\n" +
				"                String previousManagerOrganizationId = personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex - 1);\n" +
				"                Optional<EncouragementReview> previousEncouragementReview = encouragementReviewService.getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType(previousManagerOrganizationId, encouragement.getEncouragementId(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode());\n" +
				"                previousEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));\n" +
				"            }\n" +
				"            if (nextEncouragementReview.isPresent()) {\n" +
				"                if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||\n" +
				"                        encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())) {\n" +
				"                    nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));\n" +
				"                } else {\n" +
				"                    nextEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));\n" +
				"                    thisEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));\n" +
				"                }\n" +
				"            } else if (!(encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||\n" +
				"                    encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())))\n" +
				"                this.createReviewFromEncouragement(encouragement, nextManagerOrganizationId, ReviewResultEnum.UNDER_REVIEW.getCode(), ReviewTypeEnum.ORDINARY_REVIEWER.getCode(), DraftEnum.Nothing.getCode(), personnelManager.getPersonnelManagerOrganizationIdList().get(creatorIndex));\n" +
				"        } else {\n" +
				"            if (encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()) ||\n" +
				"                    encouragement.getEncouragementStatus().equals(EncouragementResultEnum.APPROVED.getCode())) {\n" +
				"                Optional<EncouragementReview> commissionEncouragementReview = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());\n" +
				"                commissionEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.deleteEncouragementReview(encouragementReview.getEncouragementReviewId()));\n" +
				"            }else {\n" +
				"                Optional<EncouragementReview> optional = encouragementReviewService.getAllCommissionReviewsForThisEncouragement(encouragement.getEncouragementId());\n" +
				"                if (optional.isEmpty()) {\n" +
				"                    commissionInput(encouragement, personnelManager.getPersonnelManagerOrganizationIdList().size() - 1 >= 0 ? personnelManager.getPersonnelManagerOrganizationIdList().get(personnelManager.getPersonnelManagerOrganizationIdList().size() - 1) : null, personnelReviewCreator);\n" +
				"                } else {\n" +
				"                    EncouragementReview commissionReview = optional.get();\n" +
				"                    encouragementReviewService.updateExistEncouragementReview(commissionReview, encouragement);\n" +
				"                    thisEncouragementReview.ifPresent(encouragementReview -> encouragementReviewService.updateExistEncouragementReview(encouragementReview, encouragement));\n" +
				"                }\n" +
				"            }\n" +
				"        }\n" +
				"    }";


//		finalOfAll("if The encouragement is rejected or approved what happened for encouragement review?");
//		finalOfAll("if user role was ROLE.ENCOURAGEMENT_SPECIALIST what happened for encouragement review?");
//		finalOfAll("if the encouragement status be SENT FOR REGISTRAR CORRECTION what happened for encouragement?");
//		finalOfAll("if the encouragementStatus be SENT_FOR_REGISTRAR_CORRECTION what happened for encouragementReviewStatus?");
		if (methodsInnerMethodList.isEmpty())
			methodsList.forEach(methodDeclaration -> extractInnerMethods(methodDeclaration.getNameAsString(), methodDeclaration.toString(), methodsCallingInnerMethodSet, allClasses));
		if (businessBlocksMapTag.isEmpty())
			tagIfAndElse();
//		finalOfAll("if the amount of encouragement be outer of Registrar Power Limits amount what happened in calculator?", allClasses);
		finalOfAll("if the encouragementReviewResult be SENT_FOR_RECENT_MANAGER_CORRECTION what happened?", allClasses);
//		finalOfAll("when the calculator method is called?", allClasses);
//		finalOfAll("when does changeEncouragementStatus to UNDER_COMMISSION_REVIEW?", allClasses);
//		finalOfAll("when does deleteEncouragementReview call?", allClasses);
		String json1 = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/updatedEmbeddingsqQ.json"));
		ObjectMapper mapper = new ObjectMapper();
		// تبدیل JSON به Map
		Map<String, String> businessBlocksMap1 = mapper.readValue(json1, Map.class);
		String question = "give me the method name where the encouragement review send to commission.";
//		String question= "how can an encouragement review be updated after status send to commission?";
//		String solution2 = getSolutionFromMultipleMethods2(question);
		Object code3="" +
				"        List<String> managerPositionList = personnelManager.getPersonnelManagerPositionList();\n" +
				"        Optional<String> creatorPositionInListOfManagersPosition = managerPositionList.stream().filter(position -> JobPositionEnum.fromCode(personnelReviewCreator.getPersonnelJobPositionCode()).equals(JobPositionEnum.fromCode(Integer.parseInt(position)))).findAny();\n" +
				"        String creatorPosition = creatorPositionInListOfManagersPosition.get();\n" +
				"        int creatorIndex = managerPositionList.indexOf(creatorPosition);";

//		String solution3 = getSolutionFromMultipleMethods3(code3);
//		answerFromCode(code, "how can an encouragement review be updated after status send to commission?", solution2);
//		String solution = getSolutionFromMultipleMethods(businessBlocksMap1, question);
		Pattern pattern = Pattern.compile("if\\s*\\(([^\\)]*\\([^\\)]*\\)[^\\)]*|[^\\)]*)\\)");
//		Matcher matcher = pattern.matcher(solution2);
//		String ifCondition = null;
//		if (matcher.find()) {
//			ifCondition= matcher.group(0); // گروه 0 همان if(...) کامل است
//			System.out.println(ifCondition);
//		}
//
//		String finalIfCondition = ifCondition;
//		Optional<Map.Entry<String, String>> optional = businessBlocksMap1.entrySet().stream().filter(stringStringEntry -> stringStringEntry.getKey().contains(finalIfCondition)).findAny();
//		String key = optional.get().getKey();
//		Optional<MethodDeclaration> declarationOptional = methodsList.stream().filter(methodDeclaration -> methodDeclaration.getName().toString().equals(key.split("/")[0].trim())).findAny();
		question= "how can an encouragement review be updated after status send to commission?";
//		getBusinessWithInnerMethod(declarationOptional.get().toString());

		String json2 = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/JavaCodeFlowChartt.json"));
//		ObjectMapper mapper = new ObjectMapper();
		// تبدیل JSON به Map
		Map<String, String> flowChart = mapper.readValue(json2, Map.class);
//		Optional<String> keyOptional = flowChart.keySet().stream().filter(s -> s.equals(declarationOptional.get().getNameAsString())).findAny();
//		System.out.println(keyOptional.get());
//		String s = flowChart.get(keyOptional.get());
		System.out.println();
//		String substring = s.replace(s.substring(s.indexOf("This flowchart")), "").substring(s.indexOf("A[")).replaceAll("(?m)^", "    ");
//		String finalS="" +
//				"graph TD\n" +
//				"    classDef centered text-align:center;\n" +
//				"\n".concat(substring);
//		System.out.println(finalS);
//		String svg = getMermaidSvg(finalS);
//        Path path = Path.of("diagram.svg");
//        try (FileWriter writer = new FileWriter(path.toFile())) {
//            writer.write(svg);
//        }
//		System.out.println("SVG generated: " + path.toAbsolutePath());


//        System.out.println("SVG generated: " + path.toAbsolutePath());
//        System.out.println(answerFromCode(codeSnippet1));
//		Gson gson = new GsonBuilder().setPrettyPrinting().create();
//		String outputFilePath= "F:/Projects/encouragement-develop/encouragement-service/JavaCodeFlowChartt.json";
//		try (FileWriter writer = new FileWriter(outputFilePath, StandardCharsets.UTF_8)) {
//			gson.toJson(methodFlowChartMap, writer);
//			System.out.println("Embeddings saved to " + outputFilePath);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}


//		String method1= "if (status != null && (status.equals(EncouragementResultEnum.SENT_FOR_CORRECTION.getCode())))\n" +
//						"                encouragement.setIsEncouragementSeen(Boolean.FALSE);";
//
//		String m2= "if (status != null && (status.equals(EncouragementResultEnum.APPROVED.getCode()) || status.equals(EncouragementResultEnum.REJECTED.getCode()) || status.equals(EncouragementResultEnum.CORRECTION_AND_APPROVAL.getCode()))) {\n" +
//				"                encouragement.setEncouragementAppliedDate(LocalDate.now());\n" +
//				"                encouragement.setEncouragementApproverOrganizationId(personnelOrganizationId);\n" +
//				"                encouragement.setEncouragementApproverType(approverType);\n" +
//				"            }";
//
//		List<Double> embeddingM1 = getEmbedding(method1);
//		List<Double> embeddingM2 = getEmbedding(m2);
//		List<Double> question = getEmbedding("when set the IsEncouragementSeen to Boolean.FALSE?");
//
//		System.out.println(cosineSimilarity(question, embeddingM1));
//		System.out.println(cosineSimilarity(question, embeddingM2));
//



//		String json = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/newBlockEmbeddings.json"));
//		Type type = new TypeToken<LinkedHashMap<String, List<Double>>>(){}.getType();
//		Map<String, List<Double>> embeddingsMap = new Gson().fromJson(json, type);



//		Gson gson = new Gson();
//		List<Double> method = getEmbedding(
//				"Method: calculator  **Analysis:**  1. `if (encouragement != null) { ... }` This condition means that the system is checking if an encouragement record exists before proceeding with further actions. The business action taken if this condition is true is:  * Retrieve the personnel associated with the encouragement 2. `if (personnelReviewCreator != null) { ... }` This condition means that the system is verifying the existence of a personnel review creator. The business action taken if this condition is true is:  * Check the registrar power limits for the personnel organization 3. `if (checkingRegistrarPowerLimits) { ... }` This condition means that the system is checking if the registrar power limits are being checked. The business action taken if this condition is true is:  * Determine if the encouraged person's rank is greater than or equal to Second Lieutenant, and if so, send the encouragement for Vedja commission  * If not, update the encouragement status 4. `if (isGreaterThanSecondLieutenant) { ... }` This condition means that the system is checking the encouraged person's rank. The business action taken if this condition is true is:  * Send the encouragement for Vedja commission 5. `} else { ... }` (belonging to 4th IF) This block of code means that the system is handling cases where the encouraged person's rank is less than or equal to Second Lieutenant. The business action taken in this block is:  * Update the encouragement status 6. `if (optional.isPresent()) { ... }` This condition means that the system is checking if registrar power limits exist for the personnel organization. The business action taken if this condition is true is:  * Check if the amount of encouragement is within the power limits 7. `if (amountOfEncouragementIsWithinThePowerLimits) { ... }` This condition means that the system is verifying if the amount of encouragement is within the power limits. The business action taken if this condition is true is:  * Update the encouragement status 8. `} else { ... }` (belonging to 6th IF) This block of code means that the system is handling cases where the amount of encouragement exceeds the power limits. The business action taken in this block is:  * Update the encouragement status and call afterCheckingRegistrarPowerLimit method.");
//		System.out.println(cosineSimilarity(question, method));


//		Map.Entry<String, List<Double>> best = embeddingsMap.entrySet().stream().max(Comparator.comparingDouble(value -> cosineSimilarity(question, value.getValue()))).orElse(null);
//		if (best != null) {
//			System.out.println("Most similar method: " + best.getKey());
//			System.out.println("Similarity: " + cosineSimilarity(question, best.getValue()));
//		}



//		List<Map.Entry<String, List<Double>>> entryList = embeddingsMap.entrySet().stream()
//				.sorted(Comparator.comparingDouble(o -> cosineSimilarity(question, o.getValue())))
//				.limit(20).toList();

//		entryList.forEach(stringListEntry ->
//				System.out.println(stringListEntry.getKey() + "->" + cosineSimilarity(question, stringListEntry.getValue())));



//		try (FileOutputStream out = new FileOutputStream("F:/Projects/encouragement-develop/encouragement-service/javaCodeEachBlockBusinesss.docx")) {
//			document.write(out);
//			System.out.println("Document written successfully!");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		String codeSnippet1 = "" +
				"    public TotalStructureDTO getTotalOrganizationStructureInfoByYear(Short year) {\n" +
				"        try {\n" +
				"            List<TotalStructureDTO> list = organizationStructure.getTotalStructure(null, null, year, null, null);\n" +
				"            Optional<TotalStructureDTO> totalStructureDTOOptional;\n" +
				"            TotalStructureDTO totalStructureDTO = null;\n" +
				"            if (list != null) {\n" +
				"                totalStructureDTOOptional = list.stream().findAny();\n" +
				"                if (totalStructureDTOOptional.isPresent()) {\n" +
				"                    totalStructureDTO = totalStructureDTOOptional.get();\n" +
				"                    totalStructureDTO.setAvailabilityPercentage(((double) totalStructureDTO.getTotalStructureAvailable() / totalStructureDTO.getTotalStructureCount()) * 100);\n" +
				"                    totalStructureDTO.setPlanCompletionPercentage(\n" +
				"                            ((double) ((totalStructureDTO.getTotalStructureAvailable() + totalStructureDTO.getTotalStructureAnnualRecruitmentCount())\n" +
				"                                    - totalStructureDTO.getTotalStructureAnnualNaturalReleaseCount())\n" +
				"                                    / totalStructureDTO.getTotalStructureCount()) * 100);\n" +
				"                    totalStructureDTO.setEndOfYearInventory((double) (totalStructureDTO.getTotalStructureAvailable() + totalStructureDTO.getTotalStructureAnnualRecruitmentCount())\n" +
				"                            - totalStructureDTO.getTotalStructureAnnualNaturalReleaseCount());\n" +
				"//                LocalDateTime dateTimeOneYearAgo = LocalDateTime.of(convertedYear - 1, 1, 1, 0, 0, 0);\n" +
				"//                String dateOneYearAgo = dateTimeOneYearAgo.format(formatter);\n" +
				"                    List<TotalStructureDTO> lastYearList = organizationStructure.getTotalStructure(null, null, (short) (year - 1), null, null);\n" +
				"                    TotalStructureDTO lastYearTotalStructure;\n" +
				"                    if (lastYearList != null) {\n" +
				"                        Optional<TotalStructureDTO> lastYearTotalStructureOptional = lastYearList.stream().findAny();\n" +
				"                        if (lastYearTotalStructureOptional.isPresent()) {\n" +
				"                            lastYearTotalStructure = lastYearTotalStructureOptional.get();\n" +
				"                            totalStructureDTO.setTotalStructureAnnualRecruitmentCountFromLastYear(lastYearTotalStructure.getTotalStructureAnnualRecruitmentCount());\n" +
				"                            totalStructureDTO.setTotalStructureAnnualAdvancementAcademicCountFromLastYear(lastYearTotalStructure.getTotalStructureAnnualAdvancementAcademicCount());\n" +
				"                            totalStructureDTO.setTotalStructureAnnualMembershipConversionCountFromLastYear(lastYearTotalStructure.getTotalStructureAnnualMembershipConversionCount());\n" +
				"                        }\n" +
				"                    }\n" +
				"                }\n" +
				"            }\n" +
				"            return totalStructureDTO;\n" +
				"        } catch (FeignException e) {\n" +
				"            if (e.status() == -1) {\n" +
				"                throw new ResponseStatusException(HttpStatus.GATEWAY_TIMEOUT, \"Request Timeout - Unauthorized\");\n" +
				"            }\n" +
				"            throw new ResponseStatusException(HttpStatus.valueOf(e.status()), \"Error: \" + e.getMessage());\n" +
				"        }\n" +
				"    }";

//		String businessWithInnerMethod = getBusinessWithInnerMethod(codeSnippet1);
//		System.out.println(businessWithInnerMethod);
//		System.out.println(answerFromCode(codeSnippet1));
//		System.out.println("detail...");
//		getDiagram(businessWithInnerMethod);
//		String businessWithInnerMethod = "    " +
//				"    A[calculator] -->|encouragement != null| B\n" +
//				"    B[if encouragedPerson exists] -->|personnelReviewCreator != null| C\n" +
//				"    C[if checkingRegistrarPowerLimits] -->|currentUserRole == ROLE.ENCOURAGEMENT_SPECIALIST| D\n" +
//				"    D[check if isGreaterThanSecondLieutenant] -->|isGreaterThanSecondLieutenant| E\n" +
//				"    E[sendForVedjaCommission] --> F\n" +
//				"    D -->|else| G[changeEncouragementStatus with personnelReviewCreator and ReviewTypeEnum.ORDINARY_REVIEWER]\n" +
//				"    F --> H[afterCheckingRegistrarPowerLimit]\n" +
//				"    G --> H\n" +
//				"    H --> I[check amountOfEncouragementIsWithinThePowerLimits]\n" +
//				"    I -->|true| J[changeEncouragementStatus]\n" +
//				"    J --> K[afterCheckingRegistrarPowerLimit]\n" +
//				"    K --> L[check if personnelManager exists and draftIsSent]\n" +
//				"    L -->|true| M[changeEncouragementStatus]\n" +
//				"    L -->|false| N[throw GeneralException: Error occurred]\n" +
//				"    M --> O[afterCheckingRegistrarPowerLimit]\n" +
//				"    O --> P[throw GeneralException: General error]\n" +
//				"    N --> P\n" +
//				"    P --> Q[throw GeneralException: Final error]";
//		ResponseEntity<byte[]> responseEntity = renderMermaidToPng(businessWithInnerMethod);
//		previewMermaidLive(businessWithInnerMethod);
		// ذخیره روی فایل (قابل دانلود یا باز شدن)
//		Path path = Paths.get("diagram.png");
//		Files.write(path, responseEntity.getBody());

		// باز کردن فایل روی ویندوز
//		Desktop.getDesktop().open(path.toFile());

//		System.out.println("Diagram saved as: " + path.toAbsolutePath());

//		methodsList.forEach(methodDeclaration -> {
//			String explanation = getBusinessWithInnerMethod(methodDeclaration.toString());
//			writeDoc(document, explanation);
//		String[] lines = explanation.split("\\R"); // split by line breaks
//		String lastLine = lines[lines.length - 1].trim();
//		if (lastLine.startsWith("innerMethods:")) {
//			List<String> innerMethods = Arrays.stream(explanation.split("[\",:\\s]+"))
//					.filter(s -> !s.isEmpty() && !s.equals("innerMethods"))
//					.collect(Collectors.toList());
//		}
//			System.out.println(innerMethods);
//			if (!innerMethods.isEmpty()) {
//				innerMethods.forEach(EncouragementServiceApplication::extractMethodName);
//			}
//
//		});

		String filePath = "F:/Projects/encouragement-develop/encouragement-service/AllMethodsExplanation12.docx";
		String keyword = "forwardNextManager";

//		ObjectMapper mapper = new ObjectMapper();
//		ArrayNode arrayNode = mapper.createArrayNode();
//
//		Map<String, List<String>> result = extractServiceCalls(
//				"F:/Projects/encouragement-develop/encouragement-service/src/main/java/com/paya/EncouragementService/controller/EncouragementController.java",
//				"encouragementService"
//		);
//
//		Map<String, List<String>> result2 = extractServiceCalls(
//				"F:/Projects/encouragement-develop/encouragement-service/src/main/java/com/paya/EncouragementService/controller/EncouragementReviewController.java",
//				"encouragementService"
//		);
//		Set<String> entryPointServices=  Stream.concat(
//				result.values().stream().flatMap(List::stream),
//				result2.values().stream().flatMap(List::stream)
//		).collect(Collectors.toSet());
//		methodsFromFile = extractMethodsFromFile(filePath);
//
//		methodsNameFromFile.addAll(methodsFromFile.stream().map(EncouragementServiceApplication::extractMethodName).toList());
//
//		for (String methodDoc : methodsFromFile) {
//			extractMethodJson(arrayNode, mapper, methodDoc, entryPointServices);
//		}
//
//		mapper.writerWithDefaultPrettyPrinter().writeValue(
//				new File("F:/Projects/encouragement-develop/encouragement-service/methods.json"),
//				arrayNode
//		);
//		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------11");

//		getFlow();
		String firstMethod =
				"""
						addOrUpdateEncouragement :

						  first method in encouragement creation
						**Innermost If-Else Blocks**
						The code starts by checking the input request. If the request is not null and contains personnel organization IDs, it proceeds to create or update encouragements. The action taken here is "create or update encouragements".
						Next, it checks the reason type for the encouragement (based on the reason ID, type, and type ID). If a valid reason type is found, it validates the request amount using this reason type.

						**Mid-Level If-Else Blocks**
						The code then iterates over each personnel organization ID in the request. For each ID, it checks if the encouraged person's membership code is not permanent (using a specific condition). If it's not permanent, an exception is thrown.
						Next, it checks if both the registrar and the encouraged person are part of the same organization (using another condition). If they are or aren't, it throws a different exception.
						If the registrar and encouraged person are in the same organization, the code checks if the encouraged person has a personnel manager and if that manager is affiliated with the registrar's organization. If this condition is met, the action taken is "create or update an encouragement".

						**Outer Blocks**
						The code then creates an encouragement and sets its type category to normal. It also checks the encouragement status and updates it accordingly (e.g., from draft to under review).
						Finally, if the encouragement has been sent for registrar correction, it changes the status to under review.

						**Final Actions**
						After creating or updating encouragements, the code saves them to the database using `encouragementRepository.saveAll()`.
						If the list of encouragements is not empty and the request contains a draft (sent), the code checks if each encouragement has been seen. If it hasn't, it calls the `calculator` method with various parameters to perform some calculation or action.
						The final action taken by this code is "calculate something" using the `calculator` method.

						In summary, the business logic and decision-making in this Java code involve:
						1. Validating input requests
						2. Creating or updating encouragements based on reason types and personnel organization IDs
						3. Checking if encouraged persons are not permanent members and have a personnel manager affiliated with the registrar's organization
						4. Updating encouragement statuses and performing calculations as needed
						""";
//		getBusiness(firstMethod);

		String updateEachEncouragementReviewThatNeededMethod =
				"""
						updateEachEncouragementReviewThatNeeded:
							
						""Here's the breakdown of the business logic and decision-making in plain human language:\\n" +
						 "The code performs updates and calculations on encouragement reviews based on the values in the dto object and the current state of the entity object.\\n\\n" +
						 
						 "Innermost If-Else Blocks\\n" +
						 "* The first block checks if entity.getIsEncouragementReviewSeen() is true.\\n" +
						 " - If true, it throws a GeneralException with the message: "این بررسی مشاهده شده است و قابل ویرایش نمی باشد." (This review has been seen and cannot be edited).\\n" +
						 "* The second block sets some properties on the entity object based on the values in the dto object.\\n\\n" +
						 
						 "Middle-Level If-Else Blocks\\n" +
						 "* The first block checks if dto.getEncouragementReviewResult() is not null and equals one of: APPROVED, CORRECTION_AND_APPROVAL, SENT_FOR_ENCOURAGEMENT_SPECIALIST, REJECT_TO_COMMISSION.\\n" +
						 " - If true, it checks if entity.getEncourourmentReviewType() is ORDINARY_REVIEWER.\\n" +
						 " - Calls the calculator method with parameters including encouragement, reasonType, and currentUser.\\n\\n" +
						 "* The second block checks if dto.getEncouragementReviewResult() is not null and equals SENT_FOR_ENCOURAGEMENT_SPECIALIST or REJECT_TO_COMMISSION.\\n" +
						 " - If true, it calls sendForVedjaCommission(encouragement, currentUser).\\n" +
						 " - Updates the entity using encouragementReviewService.updateExistEncouragementReview.\\n\\n" +
						 "* The third block checks if dto.getEncouragementReviewResult() is not null and equals APPROVED or CORRECTION_AND_APPROVAL.\\n" +
						 " - If true, it calls the calculator method with parameters including encouragement, reasonType, and currentUser.\\n\\n" +
						 
						 "Outer If-Else Blocks\\n" +
						 "* The first block checks if dto.getEncouragementReviewResult() is not null and equals REJECTED.\\n" +
						 " - If true, calls changeEncouragementStatus(encouragement, dto.getEncouragementReviewResult(), currentUser.getPersonnelOrganizationID()).\\n" +
						 " - Calls the calculator method with parameters including encouragement, reasonType, and currentUser.\\n\\n" +
						 "* The second block checks if dto.getEncouragementReviewResult() is not null.\\n" +
						 " - If true, checks if dto.getEncouragementReviewResult() equals SENT_FOR_REGISTRAR_CORRECTION or SENT_FOR_RECENT_MANAGER_CORRECTION.\\n" +
						 " - If true, calls the calculator method with parameters including encouragement, reasonType, and currentUser.\\n\\n" +
						 
						 "Summary\\n" +
						 "This code performs updates, calculations, and status changes for encouragement reviews based on the review result in dto and the current state of the encouragement entity. It handles:\\n" +
						 "* Updating properties on the entity object.\\n" +
						 "* Calling the calculator method to perform calculations based on review results.\\n" +
						 "* Sending the encouragement for commission or rejection.\\n" +
						 "* Changing the encouragement status.\\n" +
						 "* Deleting registrar reviews if necessary.\\n" +
						 "* Performing checks and throwing exceptions when certain conditions are met.\\n\\n" +
						 
						 "Business Flow Summary\\n" +
						 "1. Check if the review has been seen; if yes, block editing.\\n" +
						 "2. Update properties on the encouragement review entity.\\n" +
						 "3. Based on review results, call calculators, forward for commission, or update status.\\n" +
						 "4. Handle special cases like rejection, registrar correction, or recent manager correction.\\n" +
						 "5. Ensure all calculations and updates follow organizational rules.\\n" +
						 "6. The final outcome is an updated encouragement review with proper status and calculations performed.\\n""
								 
						""";
//		getBusiness(updateEachEncouragementReviewThatNeededMethod);
//
		String secondMethod =
				"""
						  calculator:

								"**Innermost If-Else Block**\\n" +
								"The code first checks if `encouragement` is not null. If it's not null, it finds a `PersonnelDTO` object (let's call it \\"employee\\") associated with the encouragement organization ID.\\n" +
								"Next, it checks if `personnelReviewCreator` is not null and then checks if `checkingRegistrarPowerLimits` is true.\\n\\n" +
								
								"**Action Taken (Innermost Block)**\\n" +
								"If both conditions are true, the code performs two actions:\\n" +
								"1. It checks if the encouraged employee has a rank greater than or equal to \\"Second Lieutenant\\". If yes, it sends the encouragement for Vedja Commission.\\n" +
								"2. If not, it changes the encouragement status to \\"Approved\\" with an ordinary reviewer.\\n\\n" +
								
								"**Outer If-Else Block**\\n" +
								"If `currentUserRole` equals \\"Encouragement Specialist\\", the code checks two conditions:\\n" +
								"1. If the encouraged employee's rank is greater than or equal to \\"Second Lieutenant\\", it sends the encouragement for Vedja Commission.\\n" +
								"2. Otherwise, it changes the encouragement status to \\"Approved\\" with an ordinary reviewer.\\n\\n" +
								
								"**Main If-Else Block**\\n" +
								"The main block starts by checking if `optional` (from `registrarPowerLimitsService`) is present and not empty.\\n\\n" +
								
								"**Action Taken (Main Block)**\\n" +
								"If `optional` is present:\\n" +
								"1. It checks if the encouragement amount is within the power limits or if both the max amount and duration are null.\\n" +
								"2. If true, it changes the encouragement status to \\"Approved\\" with an ordinary reviewer.\\n" +
								"3. If false (amount exceeds power limits), it performs two actions:\\n" +
								"   a. Changes the encouragement status to \\"Needs Acceptance\\".\\n" +
								"   b. Calls the `afterCheckingRegistrarPowerLimit` method.\\n\\n" +
								
								"**Action Taken if `optional` is Not Present**\\n" +
								"If `optional` is not present, it calls the `afterCheckingRegistrarPowerLimit` method.\\n\\n" +
								
								"**Last Condition**\\n" +
								"The code also checks if `encouragement` is null. If not, it throws an exception if any of the above conditions fail (e.g., \\"Personnel Review Creator\\" or \\"Encouraged Employee\\" not found).\\n\\n" +
								
								"**In Summary**\\n" +
								"* The code processes encouragement requests based on employee rank and power limits.\\n" +
								"* It updates the encouragement status according to specific conditions.\\n" +
								"* It calls additional methods (`afterCheckingRegistrarPowerLimit`) depending on the outcome.\\n" +
								"* If any condition fails, it throws exceptions.\\n"
						""";
//		getBusiness(secondMethod);

		String afterCheckingRegistrarPowerLimitMethod =
				"""
						afterCheckingRegistrarPowerLimitMethod:

								"**Innermost If-Else Blocks**\\n" +
								"The first condition checks if `isUpdateMode` is false. If it's not, then:\\n" +
								"* The method creates a review from an encouragement (`createReviewFromEncouragement`) with certain properties (organization ID, result code, reviewer type, draft code, and null).\\n\\n" +

								"**Outermost If-Else Blocks**\\n" +
								"The second condition checks two things:\\n" +
								"1. Whether `encouragedPerson.getPersonnelManager()` is not null.\\n" +
								"2. Whether the current draft equals `DraftEnum.Sent.getCode()`.\\n" +
								"If both conditions are true, then:\\n" +
								"* The method calls another method (`forwardEncouragementForNextStep`) with several parameters (encouragement, personnel review creator, encouraged person, next manager, delete recent manager review, and current user role).\\n\\n" +

								"**Else Block**\\n" +
								"If the above conditions are not met, then:\\n" +
								"* A `GeneralException` is thrown with a message \\"لیست مدیران شخص وارد شده خالی می باشد .\\" (The list of personal managers entered is empty).\\n\\n" +

								"**In Summary**\\n" +
								"1. If `isUpdateMode` is false, create a review from an encouragement.\\n" +
								"2. If the encouraged person has a manager and the draft is sent, forward the encouragement for the next step.\\n" +
								"3. If neither condition is met, throw a general exception indicating that the list of personal managers is empty.\\n"
						""";
//		getBusiness(afterCheckingRegistrarPowerLimitMethod);

		String forwardEncouragementForNextStepMethod =
				"""
						forwardEncouragementForNextStep:

								"**Innermost If-Else Blocks**\\n" +
								"1. `if (currentUserRole.equals(RoleConstant.ROLE.ENCOURAGEMENT_SPECIALIST.getValue()) || creatorPositionInListOfManagersPosition.isEmpty() && !encouragement.getEncouragementStatus().equals(EncouragementResultEnum.REJECTED.getCode()))`\\n" +
								"   * If the current user role is an Encouragement Specialist or if there's no matching position for the encouragement creator in the list of managers' positions, and the encouragement status is not rejected.\\n" +
								"   * **Action Taken:** Call `commissionInput` with some parameters (encouragement, organization ID, and personnel review creator) and return.\\n\\n" +

								"**Outer Blocks**\\n" +
								"1. If the condition above is true, there's no need to check anything else, so the method returns after calling `commissionInput`.\\n" +
								"2. If the condition above is false, then:\\n" +
								"   * Get the creator position from the list of managers' positions.\\n" +
								"   * Find the index of this creator position in the list.\\n" +
								"3. Check if there are any encouragement reviews for this registrar and encouragement (using the `encouragementReviewService`).\\n" +
								"   * If there are reviews, get the first review (assuming it's the most recent one).\\n" +
								"4. Now, based on a boolean flag (`nextManager`), decide which method to call:\\n" +
								"   * If `nextManager` is true, call `forwardNextManager`.\\n" +
								"   * Otherwise, call `forwardPreviousManager`.\\n\\n" +

								"These two methods are not shown in this code snippet, but they presumably perform different actions depending on whether the encouragement should be forwarded to the next manager or the previous one.\\n\\n" +

								"**In Summary**\\n" +
								"This method checks if the current user has a specific role (Encouragement Specialist) or if there's no matching position for the encouragement creator. If true, it calls `commissionInput`. Otherwise, it determines which manager (next or previous) to forward the encouragement to based on some conditions and calls either `forwardNextManager` or `forwardPreviousManager`.\\n"
						""";
//		getBusiness(forwardEncouragementForNextStepMethod);

		String commissionInputMethod =
				"""
						commissionInputMethod:
								
								"**Business Logic and Decision-Making Breakdown**\\n" +
								"The method `commissionInput` processes an encouragement object and its related data. The code checks two conditions: one for creating a new review and another for updating an existing review.\\n\\n" +

								"**Innermost If-Else Block**\\n" +
								"If there are no reviews associated with this encouragement (i.e., the `Optional<EncouragementReview>` is empty), then:\\n" +
								"* Create a new review from the encouragement object.\\n" +
								"  + Set the review result to \\"UNDER_COMMISSION_REVIEW\\".\\n" +
								"  + Set the review type to \\"ORDINARY_COMMISSION\\".\\n" +
								"  + Set the draft status to \\"Nothing\\".\\n" +
								"  + Assign the personnel manager.\\n\\n" +

								"**Else Block**\\n" +
								"If there are reviews associated with this encouragement, then:\\n" +
								"* Update the existing review using the `updateExistEncouragement` method.\\n" +
								"  + Pass in the retrieved review and the original encouragement object.\\n\\n" +

								"**Outer If Statement**\\n" +
								"If the encouragement status is not \\"NEED FOR ACCEPT\\", then:\\n" +
								"* Change the encouragement status to \\"UNDER COMMISSION REVIEW\\".\\n" +
								"  + Use the `changeEncouragementStatus` method.\\n" +
								"  + Update the personnel organization ID.\\n" +
								"  + Set the review type to \\"ORDINARY COMMISSION\\".\\n\\n" +

								"**In Summary**\\n" +
								"This code handles two scenarios: creating a new review when there are no existing reviews and updating an existing review if one exists. It also updates the encouragement status if it's not already in the \\"NEED FOR ACCEPT\\" state.\\n
						""";
//		getBusiness(commissionInputMethod);

		String forwardPreviousManagerMethod =
				"""
							forwardPreviousManager:
							
							"**Business Logic and Decision-Making Breakdown**\\n" +
								 "Let's break down the code step by step and focus on the business logic and decision-making.\\n\\n" +
								 
								 "**Innermost If-Else Blocks**\\n" +
								 "1. The first `if` statement checks if the creator index is greater than or equal to 0. If true, it sets up variables for the previous and next encouragement reviews.\\n" +
								 "2. The second `if` statement checks if the creator index plus 1 is less than or equal to the size of the personnel manager's organization ID list minus 1. If true, it gets the review of the next registrar and deletes it.\\n\\n" +
								 
								 "**Next Level of Blocks**\\n" +
								 "1. The first `if` statement checks if the current encouragement review is present. If true:\\n" +
								 "   * It checks if the result of the previous review is \\"Sent for recent manager correction\\". If true, it changes the encouragement status to \\"sent for correction\\".\\n" +
								 "2. The second `if` statement checks if the commission encouragement reviews are present. If true, it deletes them.\\n\\n" +
								 
								 "**Middle-Level Blocks**\\n" +
								 "1. The first `if` statement checks if the encouragement status is not equal to \\"Rejected\\". If true:\\n" +
								 "   * It checks if the previous encouragement review is empty and the encouragement has been seen. If true, it creates a new review from the encouragement.\\n" +
								 "   * It sends the encouragement for the registrar.\\n\\n" +
								 
								 "**Outer Blocks**\\n" +
								 "1. The first `if` statement checks if the previous encouragement review is present and delete recent manager review is true. If true, it deletes the review and forwards to the next manager.\\n" +
								 "2. The second `else` block updates the existing encouragement review if the previous review's organization ID does not match the encouragement registrar's organization ID.\\n\\n" +
								 
								 "**Final Blocks**\\n" +
								 "1. The `else` block (i.e., when the creator index is less than 0) throws a GeneralException with the message \\"مدير قبلي ايي وجود ندارد\\" (Manager is not present).\\n\\n" +
								 
								 "**In Summary**\\n" +
								 "This code:\\n" +
								 "* Checks if there is a previous manager and sets up variables accordingly.\\n" +
								 "* Deletes reviews that are no longer needed.\\n" +
								 "* Updates or creates new reviews based on encouragement status and whether it has been seen.\\n" +
								 "* Sends the encouragement for the registrar when necessary.\\n" +
								 "* Handles cases where the encouragement review needs to be deleted or updated.\\n\\n" +
								 "The business logic revolves around managing encouragement reviews, updating their statuses, and sending them to the next manager in the chain.\\n"
								 
						""";
//		getBusiness(forwardPreviousManagerMethod);

		String deleteRegistrarReviewIfExistMethod =
				"""
						deleteRegistrarReviewIfExist:
							
						"**Business Logic and Decision-Making Breakdown: Checking Under Review Ordinary Review**\\n" +
						 "Here's the breakdown of the business logic and decision-making in plain human language:\\n" +
						 "The code is checking if a certain type of review exists for a given encouragement (which represents a positive feedback or encouragement). The review being checked is one that has an \\"Under Review\\" status, which implies it needs further evaluation before being finalized. The reviewer type being checked is ordinary.\\n\\n" +
						 
						 "**Innermost If-Else Block**\\n" +
						 "* The code checks if a review exists for the given encouragement with the specific reviewer type and under review status.\\n" +
						 "  - If such a review exists (action taken: proceed to delete it)\\n\\n" +
						 
						 "**Outer Block**\\n" +
						 "* Based on the result of the innermost check, the code either deletes the existing review or doesn't perform any action.\\n\\n" +
						 
						 "**Summary**\\n" +
						 "This code checks if there's an \\"Under Review\\" ordinary review for a specific encouragement and then decides whether to delete that review (if found) or do nothing (if not found).\\n\\n"
								 
						""";
//		getBusiness(deleteRegistrarReviewIfExistMethod);

		String sentForRegistrarMethod =
				"""
						sentForRegistrar:
							
						 "Here's the breakdown of the business logic and decision-making in plain human language:\\n" +
						 "The method `sentForRegistrar` is processing an \\"encouragement\\" object. It's checking if the current registrar organization ID matches the previous manager organization ID. If they don't match, it's looking for a review record related to this encouragement and the previous manager organization ID. If such a review exists, it deletes that review.\\n\\n" +
						 
						 "**Action Steps**\\n" +
						 "1. Change the status of the encouragement to \\"SENT FOR CORRECTION\\".\\n" +
						 "2. Check if there is already an existing review record for the current registrar organization ID and this encouragement.\\n" +
						 "   - If not, create a new review with the status \\"UNDER REVIEW\\" and assign it to the ordinary reviewer (i.e., the registrar).\\n" +
						 "   - If there is an existing review record, update that review by linking it to the current encouragement.\\n\\n" +
						 
						 "**Summary**\\n" +
						 "1. Check if registrar organizations match; if not, delete any previous reviews.\\n" +
						 "2. Update encouragement status to \\"SENT FOR CORRECTION\\".\\n" +
						 "3. Check if a review exists for the current registrar and this encouragement; if not, create one.\\n" +
						 "4. If a review exists, update it by linking it to the current encouragement.\\n" +
						 "These actions are taken based on the logic of managing encouragements and reviews in an organization, where different registrars may be involved at different stages of the process.\\n\\n"
								 
						""";
//		getBusiness(sentForRegistrarMethod);

		String forwardNextManagerMethod =
				"""
						forwardNextManager:
							
						"Let's break down the code step by step and describe the business logic in plain human language.\\n\\n" +
						 
						"**Innermost If-Else Block**\\n" +
						"* If `deleteRecentManagerReview` is true, delete any existing review for the recent manager.\\n" +
						"* No action taken if `deleteRecentManagerReview` is false.\\n\\n" +
						 
						"**Outer If-Else Block (First Condition)**\\n" +
						"* If there's a next manager in the list (`creatorIndex + 1 < managerPositionList.size()`), proceed with checking the next manager's encouragement review status.\\n" +
						"  + Get the next manager's organization ID and the corresponding encouragement review, if it exists.\\n" +
						"  + If the previous manager has an existing review for this encouragement, delete it.\\n" +
						"* If there is no next manager or the creator index is out of bounds, jump to the outer if-else block (second condition).\\n\\n" +
						 
						"**Outer If-Else Block (Second Condition)**\\n" +
						"* If the encouragement status is REJECTED or APPROVED, delete any existing reviews for all managers, including the commission review.\\n" +
						"* If the encouragement status is not REJECTED or APPROVED:\\n" +
						"  + Create a new review for the next manager with an UNDER_REVIEW status and Ordinary Reviewer type.\\n" +
						"  + Update the existing review for this encouragement, if it exists.\\n\\n" +
						 
						"**Summary**\\n" +
						"The code checks the encouragement status and the position of the current manager in the list. Based on these conditions, it either deletes or updates existing reviews, creates new reviews, or performs other actions to forward the next manager's encouragement review process.\\n"
								 
						""";
//		getBusiness(forwardNextManagerMethod);


//		getFlow(methodsList);


//		System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------22");

//		List<String> results = searchInDocx(filePath, keyword);
//		String text = results.get(0);
//		List<String> parts = splitTextBySentenceExceptSummary(text);

//		getFlow();

//		String[] rawChunks = text.split("\\r?\\n");
//		StringBuilder numberedText = new StringBuilder();
//		for (int i = 0; i < rawChunks.length; i++) {
//			numberedText.append((i + 1) + "️⃣ " + rawChunks[i].trim() + "\n\n");
//		}
//		RestTemplate restTemplate = new RestTemplate();
//		HttpHeaders headers = new HttpHeaders();
//		headers.add("Content-Type", "application/json");
//		Map<String, Object> body = new HashMap<>();
//		body.put("model", "llama3");
////		body.put("prompt", "Would you please explain this Java code in plain human language to understand the business?Please explain this Java code in plain human language, focusing ONLY on the **business logic and decision-making**. \n" +
////				"Do NOT explain method parameters, variable values, or the inputs of any called methods. \n" +
////				"Just describe the conditions being checked so carefully and do not miss any condition in complex if statement and the **general action taken** in the THEN or ELSE blocks, e.g., \"perform a calculation\" or \"change the status\", without listing the arguments." +
////				"Analyze the code step by step starting from the innermost if-else blocks. For each inner block, determine the action taken. Then move outward to the outer blocks, combining the results to get the final business logic\n"
////				+ codeSnippet);
//		body.put("prompt", "You are a reasoning model that selects the most relevant part of a text to answer a question.\n" +
//				"\n" +
//				"Rules:\n" +
//						"1. If the question is \"What does ... do?\" OR a general conceptual/business/functional question:\n" +
//						"   - Ignore the detailed sentences.\n" +
//						"   - Focus only on the \"summary\" section of the text.\n" +
//						"   - Return the **summary text exactly as it appears** as the answer.\n" +
//						"\n" +
//						"2. Otherwise:\n" +
//						"   - Analyze the detailed sentences (numbered 1 to 21).\n" +
//						"   - Identify which sentence best answers the question.\n" +
//						"   - Return its number and the text exactly as it appears.\n" +
//				"\n" +
//				"Question:\n" +
//				"\"What happen if the role was ENCOURAGEMENT_SPECIALIST?\"\n" +
//				"\n" +
//				"Text:" +
//				parts.toString() +
//				"\n" +
//				"Answer format:\n" +
//				"Part: [number]\n" +
//				"Reason: [brief reasoning]" +
//				"and at the end tell me a simple answer from that part ypu have been decided. ask me question if you need.\n");
//		body.put("stream", false);
//		String url = "http://localhost:11434/api/generate";
//		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
//		if (response.getStatusCode().equals(HttpStatus.OK)) {
//			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
//			System.out.println(response.getBody().getResponse());
////			XWPFParagraph titlePara = document.createParagraph();
////			titlePara.createRun().setBold(true);
////			// اضافه کردن توضیح متد
////			XWPFParagraph paragraph = document.createParagraph();
////			paragraph.createRun().setText(response.getBody().getResponse());
//		}
	}

	private static void finalOfAll(String question, Map<String, CompilationUnit> allClasses) throws IOException {
//		Map<String, List<String>> innerMethodMap2 = new HashMap<>();
//		List<String> list2 = new ArrayList<>();
//		list2.add("deleteRegistrarReviewIfExist");
//		list2.add("createReviewFromEncouragement");
//		list2.add("getAllCommissionReviewsForThisEncouragement");
//		list2.add("deleteEncouragementReview");
//		list2.add("commissionInput");
//		list2.add("updateExistEncouragementReview");
//		innerMethodMap2.put("forwardNextManager", list2);
//
//		Map<String, List<String>> innerMethodMap3 = new HashMap<>();
//		List<String> list3 = new ArrayList<>();
//		list3.add("calculator");
//		innerMethodMap3.put("addOrUpdateEncouragement", list3);
//
//		Map<String, List<String>> innerMethodMap4 = new HashMap<>();
//		List<String> list4 = new ArrayList<>();
//		list4.add("calculator");
//		innerMethodMap4.put("addOrUpdateEncouragementByEncouragementSpecialist", list4);
//
//		Map<String, List<String>> innerMethodMap5 = new HashMap<>();
//		List<String> list5 = new ArrayList<>();
//		list5.add("calculator");
//		innerMethodMap5.put("updateEachEncouragementReviewThatNeeded", list5);
//
//		Map<String, List<String>> innerMethodMap6 = new HashMap<>();
//		List<String> list6 = new ArrayList<>();
//		list6.add("changeEncouragementStatus");
//		list6.add("APPROVED");
//		list6.add("NEED_FOR_ACCEPT");
//		innerMethodMap6.put("calculator", list6);
//
//		Map<String, List<String>> innerMethodMap7 = new HashMap<>();
//		List<String> list7 = new ArrayList<>();
//		list7.add("changeEncouragementStatus");
//		list7.add("UNDER_COMMISSION_REVIEW");
//		innerMethodMap7.put("commissionInput", list7);
//
//		Map<String, List<String>> innerMethodMap8 = new HashMap<>();
//		List<String> list8 = new ArrayList<>();
//		list8.add("changeEncouragementStatus");
//		list8.add("SENT_FOR_CORRECTION");
//		innerMethodMap8.put("forwardPreviousManager", list8);
//
//		Map<String, List<String>> innerMethodMap9 = new HashMap<>();
//		List<String> list9 = new ArrayList<>();
//		list9.add("changeEncouragementStatus");
//		list9.add("REJECTED");
//		list9.add("fromDTO");
//		innerMethodMap9.put("updateEachEncouragementReviewThatNeeded", list9);
//
//		Map<String, List<String>> innerMethodMap10 = new HashMap<>();
//		List<String> list10 = new ArrayList<>();
//		list10.add("changeEncouragementStatus");
//		list10.add("UNDER_VEDJA_REVIEW");
//		innerMethodMap10.put("sendForVedjaCommission", list10);
//
//		Map<String, List<String>> innerMethodMap11 = new HashMap<>();
//		List<String> list11 = new ArrayList<>();
//		list11.add("deleteEncouragementReview");
//		list11.add("getAllCommissionReviewsForThisEncouragement");
//		innerMethodMap11.put("forwardNextManager", list11);
//
//
//		Map<String, List<String>> innerMethodMap12 = new HashMap<>();
//		List<String> list12 = new ArrayList<>();
//		list12.add("deleteEncouragementReview");
//		list12.add("getAllCommissionReviewsForThisEncouragement");
//		innerMethodMap12.put("forwardPreviousManager", list12);
//
//
//		Map<String, List<String>> innerMethodMap13 = new HashMap<>();
//		List<String> list13 = new ArrayList<>();
//		list13.add("deleteEncouragementReview");
//		list13.add("getReviewOfThisRegistrarThisEncouragementWithUnderReviewStatusAndReviewerType");
//		innerMethodMap13.put("sentForRegistrar", list13);
//
//		List<Map<String, List<String>>> allInnerMap = new ArrayList<>();
//		allInnerMap.add(innerMethodMap2);
//		allInnerMap.add(innerMethodMap3);
//		allInnerMap.add(innerMethodMap4);
//		allInnerMap.add(innerMethodMap5);
//		allInnerMap.add(innerMethodMap6);
//		allInnerMap.add(innerMethodMap7);
//		allInnerMap.add(innerMethodMap8);
//		allInnerMap.add(innerMethodMap9);
//		allInnerMap.add(innerMethodMap10);
//		allInnerMap.add(innerMethodMap11);
//		allInnerMap.add(innerMethodMap12);
//		allInnerMap.add(innerMethodMap13);


//		List<String> methods = new ArrayList();
//		methods.add("updateEachEncouragementReviewThatNeeded");
//		methods.add("updateEncouragementReview");
//		methods.add("forwardNextManager");
//		methods.add("sentForRegistrar");
//		methods.add("deleteRegistrarReviewIfExist");
//		methods.add("forwardPreviousManager");
//		methods.add("commissionInput");
//		methods.add("afterCheckingRegistrarPowerLimit");
//		methods.add("calculator");
//		methods.add("addOrUpdateEncouragement");
//		methods.add("deleteEncouragementReview");
//		methods.add("createReviewFromEncouragement");
//		methods.add("changeEncouragementStatus");
//		methods.add("getAllCommissionReviewsForThisEncouragement");
//		methods.add("updateExistEncouragementReview");

		if (question.startsWith("if")) {
			List<String> optional;

			String questionLower = question.toLowerCase();
			Set<String> questionWords = new HashSet<>(Arrays.asList(questionLower.split("\\W+")));

			Map<String, Integer> bestKey = new HashMap<>();
			int maxMatches = -1;
			LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
			for (Map.Entry<String, Set<String>> entry : businessBlocksMapTag.entrySet()) {
				String key = entry.getKey();
				Set<String> keyWords = new HashSet<>(Arrays.asList(key.replace("get", "").replace("if", "").toLowerCase().split("\\W+")));
				keyWords.addAll(entry.getValue().stream().map(String::toLowerCase).toList());

				int matches = 0;
				for (String qw : questionWords) {
					if (keyWords.contains(qw)) {
						matches++;
					}
				}
				if (matches > maxMatches) {
					if (matches >= 1) {
						bestKey.clear();
					}
					maxMatches = matches;
					bestKey.put(key, matches);
				} else if (matches == maxMatches && matches > 0) {
					bestKey.put(key, matches);
				}
				linkedHashMap = bestKey.entrySet().stream().sorted(Map.Entry.<String, Integer>comparingByValue().reversed()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
			}
			Integer first = linkedHashMap.entrySet().iterator().next().getValue();
			optional = linkedHashMap.entrySet().stream().filter(stringIntegerEntry -> stringIntegerEntry.getValue() > 0).map(Map.Entry::getKey).collect(Collectors.toList());
//			else {
//				businessBlocksMap.keySet().
//			}

//			List<String> questionWords = Arrays.stream(question.toLowerCase().split(" ")).toList()
//					.stream().map(s -> s.replaceAll("encouragement", "").replaceAll("encouragementreview", "")).collect(Collectors.toList());
//			questionWords.removeIf(s -> s.trim().isEmpty());
//			keysWord.removeIf(s -> s.trim().isEmpty());
//			Set<String> commonKeyWord = new HashSet<>(keysWord); // حذف تکراری‌ها
//			Set<String> common = new HashSet<>();
//			for (String s1: commonKeyWord) {
//				for (String s2: questionWords) {
//					if (s1.contains(s2) || s2.contains(s1))
//						common.add(s1);
//				}
//			}
//
//			List<String> optional= new ArrayList<>();
//			for (String s1: businessBlocksMap.keySet()) {
//				for (String s2: common) {
//					if (s1.contains(s2))
//						optional.add(s1);
//				}
//			}

//			question= "give me the block where The encouragement is rejected or approved.";

			List<String> methodsContain = optional.stream().map(s -> Arrays.stream(s.split("/")).toList().get(0).trim()).toList();
			List<MethodDeclaration> methodDeclarationList = methodsList.stream().filter(methodDeclaration -> methodsContain.contains(methodDeclaration.getName().toString())).toList();
			methodDeclarationList.forEach(s -> answerFromCodeWithBlock(s.getNameAsString(), s.toString(), question));
//			String solution = getSolutionFromMultipleMethods(businessBlocksMap, question);
		} else if (question.startsWith("when")) {
//			String questionLower = question.toLowerCase();
			Set<String> questionWords = new HashSet<>(Arrays.asList(question.split("\\W+")));
			questionWords.remove("the");
			questionWords.remove("is");
			questionWords.remove("when");
			Set<String> outerMethodList = new HashSet<>();
			String innerMethod = null;

			int maxMatches = -1;
			outerMethodList.clear();

			for (Map<String, Set<String>> stringListMap : methodsInnerMethodList) {
				for (Map.Entry<String, Set<String>> entry : stringListMap.entrySet()) {
					String key = entry.getKey();
					Set<String> value = entry.getValue();
					int matches = 0;
					for (String qw : questionWords) {
						for (String eachValue : value) {
							if (qw.length() > 2 && eachValue.contains(qw)) {
								matches++;
								if (innerMethod == null)
									innerMethod = eachValue;
							}
						}
					}

					if (matches > maxMatches) {
						if (matches >= 1) {
							outerMethodList.clear();
						}
						maxMatches = matches;
						outerMethodList.add(key);
					} else if (matches == maxMatches && matches > 0) {
						outerMethodList.add(key);
					}
				}
			}


//			List<MethodDeclaration> outerDeclaration = methodsList.stream().filter(methodDeclaration -> outerMethodList.contains(methodDeclaration.getName().toString())).toList();
//			String finalInnerMethod = innerMethod;
//			outerDeclaration.forEach(methodDeclaration -> {
//				answerFromCodeWithInnerMethod(methodDeclaration.toString(), finalInnerMethod, question);
//				for (Map.Entry<String, CompilationUnit> entry : allClasses.entrySet()) {
//					CompilationUnit cu = entry.getValue();
//					cu.accept(new VoidVisitorAdapter<Void>() {
//
//						@Override
//						public void visit(MethodDeclaration md, Void arg) {
//							super.visit(md, arg);
//
//							md.accept(new VoidVisitorAdapter<Void>() {
//								@Override
//								public void visit(MethodCallExpr call, Void arg) {
//									super.visit(call, arg);
//									Optional<MethodDeclaration> parentMethod = call.findAncestor(MethodDeclaration.class);
//									if (call.getNameAsString().equals(methodDeclaration.getNameAsString())) {
//										String newQuestion = "when the " + methodDeclaration.getNameAsString() + " is called?";
//										System.out.println("##################################################################################################################################");
//										System.out.println("this method: " + methodDeclaration.getNameAsString() + "is called in: " + parentMethod.get().getNameAsString());
//										parentMethod.ifPresent(declaration -> answerFromCodeWithInnerMethod(declaration.toString(), methodDeclaration.getNameAsString(), newQuestion));
//									}
//								}
//							}, null);
//						}
//
//					}, null);
//				}
//				System.out.println("##################################################################################################################################");
//				System.out.println("this is the final entry point in the call chain and it is invoked directly by the frontend (the users action triggers this method)");
//			});

			String finalInnerMethod = innerMethod;
//			List<List<Map<String, Object>>> lists = blocksMap.values().stream().toList();
			List<Map<String, Object>> mapList = blocksMap.values().stream().toList().get(0).stream().toList();
			blocksMap.forEach((s, maps) -> {
				maps.forEach(stringObjectMap -> {
					if (stringObjectMap.values().toString().contains(finalInnerMethod)) {
						answerFromCodeWithBlockAndInnerMethod(s, finalInnerMethod, stringObjectMap.get("conditions").toString(), stringObjectMap.get("calledMethod").toString());
					}
				});
			});
//			lists.forEach(maps -> {
//				maps.stream().filter(stringObjectMap -> stringObjectMap.entrySet().stream().toList().get(1).getValue().toString().contains(finalInnerMethod))
//						.forEach(stringObjectMap ->
//								answerFromCodeWithBlockAndInnerMethod(blocksMap.keySet().toString(), finalInnerMethod, stringObjectMap.get("conditions").toString(), stringObjectMap.get("calledMethod").toString()));
//			});
			mapList.stream().filter(stringObjectMap -> stringObjectMap.entrySet().stream().toList().get(1).getValue().toString().contains(finalInnerMethod))
					.forEach(stringObjectMap ->
							answerFromCodeWithBlockAndInnerMethod(blocksMap.keySet().toString(), finalInnerMethod, stringObjectMap.get("conditions").toString(), stringObjectMap.get("calledMethod").toString()));
//			blocksMap.entrySet().stream().filter(stringStringEntry -> stringStringEntry.getValue().contains(finalInnerMethod)).forEach(stringStringEntry -> {
//				String key = stringStringEntry.getKey();
//				String[] split = key.split("/");
//				answerFromCodeWithBlockAndInnerMethod(split[0], finalInnerMethod, split[1], stringStringEntry.getValue().toString());
//			});
		}
	}

	public static void extractInnerMethods(String methodName, String methodCode, Set<String> methodsCallingInnerMethod, Map<String, CompilationUnit> allClasses) {
		List<String> methods = new ArrayList();
		methods.add("updateEachEncouragementReviewThatNeeded");
		methods.add("updateEncouragementReview");
		methods.add("forwardNextManager");
		methods.add("sentForRegistrar");
		methods.add("deleteRegistrarReviewIfExist");
		methods.add("forwardPreviousManager");
		methods.add("commissionInput");
		methods.add("afterCheckingRegistrarPowerLimit");
		methods.add("calculator");
		methods.add("addOrUpdateEncouragement");
		methods.add("deleteEncouragementReview");
		methods.add("createReviewFromEncouragement");
		methods.add("changeEncouragementStatus");
		methods.add("getAllCommissionReviewsForThisEncouragement");
		methods.add("updateExistEncouragementReview");

		if (methodsCallingInnerMethod.contains(methodName)) {
			Set<String> list = getListOfRelated(methodCode);
			methodsList.stream().filter(methodDeclaration -> list != null && list.contains(methodDeclaration.getNameAsString())).forEach(methodDeclaration -> {
				Set<String> ofRelated = getListOfRelated(methodDeclaration.toString());
				HashMap<String, Set<String>> map = new HashMap<>();
				map.put(methodDeclaration.getNameAsString(), ofRelated);
				methodsInnerMethodList.add(map);
				getParent(methodDeclaration.toString(), methodDeclaration.getNameAsString(), allClasses);
			});
			HashMap<String, Set<String>> map = new HashMap<>();
			map.put(methodName, list);
			methodsInnerMethodList.add(map);
			getParent(methodCode, methodName, allClasses);
		}
	}

	private static void getParent(String methodCode, String nameAsString, Map<String, CompilationUnit> allClasses) {

		for (Map.Entry<String, CompilationUnit> entry : allClasses.entrySet()) {
			CompilationUnit cu = entry.getValue();
			cu.accept(new VoidVisitorAdapter<Void>() {

				@Override
				public void visit(MethodDeclaration md, Void arg) {
					super.visit(md, arg);

					md.accept(new VoidVisitorAdapter<Void>() {
						@Override
						public void visit(MethodCallExpr call, Void arg) {
							super.visit(call, arg);

							if (call.getNameAsString().equals(nameAsString)) {
								Set<String> list = getListOfRelated(md.toString());
								HashMap<String, Set<String>> map = new HashMap<>();
								map.put(md.getNameAsString(), list);
								methodsInnerMethodList.add(map);
							}
						}
					}, null);
				}

			}, null);
		}
	}

	private static Set<String> getListOfRelated(String methodCode) {
		List<String> result = new ArrayList<>();

		JavaParser javaParser = new JavaParser();

		ParseResult<CompilationUnit> parseResult =
				javaParser.parse("class Temp { " + methodCode + " }");

		CompilationUnit cu = parseResult.getResult().orElse(null);
		if (cu == null) return null;

		MethodDeclaration outerMethod = cu.findFirst(MethodDeclaration.class).orElse(null);
		if (outerMethod == null) return null;
//
//			outerMethod.findAll(ObjectCreationExpr.class).forEach(obj -> {
//				obj.getAnonymousClassBody().ifPresent(body -> {
//					body.stream()
//							.filter(n -> n instanceof MethodDeclaration)
//							.map(n -> (MethodDeclaration) n)
//							.forEach(inner ->
//									result.add("AnonymousInnerMethod: " + inner.getNameAsString())
//							);
//				});
//			});

//		outerMethod.findAll(ClassOrInterfaceDeclaration.class).forEach(localClass -> {
//			localClass.getMethods().forEach(m ->
//					result.add("LocalClassMethod: " + m.getNameAsString())
//			);
//		});
//
//		outerMethod.findAll(LambdaExpr.class).forEach(lambda ->
//				result.add("LambdaBody: " + lambda.getBody().toString())
//		);
		Set<String> values = Arrays.stream(ReviewResultEnum.values()).toList().stream().map(Enum::toString).collect(Collectors.toSet());
		Set<String> values2 = Arrays.stream(EncouragementResultEnum.values()).toList().stream().map(Enum::toString).collect(Collectors.toSet());
		values2.addAll(values);
		Set<String> set = values2.stream()
				.filter(methodCode::contains)
				.collect(Collectors.toSet());

		Set<String> list = outerMethod.findAll(MethodCallExpr.class).stream().map(NodeWithSimpleName::getNameAsString).collect(Collectors.toSet());
		list.addAll(set);
		return list;
	}

	private static void tagIfAndElse() throws IOException {
		String json = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/blockMapWithTotalCodeWithNegativeElse.json"));
		ObjectMapper mapper = new ObjectMapper();
		businessBlocksMap= mapper.readValue(json, Map.class);
		String getterRegex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\.get([A-Z][a-zA-Z0-9_]*)\\s*\\(";
		Pattern pattern = Pattern.compile(getterRegex);
		businessBlocksMap.entrySet().forEach(s -> {
			Matcher matcher = pattern.matcher(s.getKey());
			Set<String> result = new LinkedHashSet<>();
			while (matcher.find()) {
				String fullGetter = matcher.group(0);
				fullGetter = fullGetter.replace("get", "").replace("if", "");
				// remove trailing "("
				fullGetter = fullGetter.substring(0, fullGetter.length() - 1);
				result.add(fullGetter);
			}

			String setterRegex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\.set([A-Z][a-zA-Z0-9_]*)\\s*\\(";
			Pattern pattern1 = Pattern.compile(setterRegex);
			Matcher matcher1 = pattern1.matcher(s.getValue());
			while (matcher1.find()) {
				String fullGetter = matcher1.group(0);
				fullGetter = fullGetter.replace("set", "");
				// remove trailing "("
				fullGetter = fullGetter.substring(0, fullGetter.length() - 1);
				result.add(fullGetter);
			}

			String methodRegex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\.([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\(";
			Pattern pattern3 = Pattern.compile(methodRegex);
			Matcher matcher3 = pattern3.matcher(s.getValue());
			while (matcher3.find()) {
				String fullCall = matcher3.group(1) + "." + matcher3.group(2).toLowerCase();
				String[] split = fullCall.split("\\.");

				String target = split[1];

				if (target.contains("encouragementreview".toLowerCase())) {
					String substring = target.substring(target.indexOf("encouragementreview"));
					// Capitalize first letter
					result.add(substring);
				}
			}

			businessBlocksMapTag.put(s.getKey(), result);
		});
	}


	private static String getSolutionFromMultipleMethods3(Object code3) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");



		String prompt =
				"You are an expert Java business analyst.\n" +
						"\n" +
						"You are given the following Java code snippet:\n" +
						code3 + "\n" +
						"\n" +
						"Task:\n" +
						"- Analyze the code and identify the main variables.\n" +
						"- For each variable, explain its purpose and meaning in plain business language.\n" +
						"  Do NOT describe Java syntax, types, or technical details; describe the role of the variable in business terms.\n" +
						"- Output the result as a JSON object mapping variable names to their business explanation.\n" +
						"- Only include variables defined in the given code snippet.\n" +
						"- Output example format:\n" +
						"{\n" +
						"  \"managerPositionList\": \"<business meaning>\",\n" +
						"  \"creatorPositionInListOfManagersPosition\": \"<business meaning>\",\n" +
						"  ...\n" +
						"}";

		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println(response.getBody().getResponse());
			return response.getBody().getResponse();
//			methodsList.add(method.split("\n")[0].trim().concat(response.getBody().getResponse()));
//			XWPFParagraph titlePara = document.createParagraph();
//			titlePara.createRun().setBold(true);
//			// اضافه کردن توضیح متد
//			XWPFParagraph paragraph = document.createParagraph();
//			paragraph.createRun().setText(response.getBody().getResponse());
		}

		return url;
	}

	private static String getInnerMethod(String question) {

		List<String> methods = new ArrayList();
		methods.add("updateEachEncouragementReviewThatNeeded");
		methods.add("updateEncouragementReview");
		methods.add("forwardNextManager");
		methods.add("sentForRegistrar");
		methods.add("deleteRegistrarReviewIfExist");
		methods.add("forwardPreviousManager");
		methods.add("commissionInput");
		methods.add("afterCheckingRegistrarPowerLimit");
		methods.add("calculator");
		methods.add("addOrUpdateEncouragement");
		methods.add("deleteEncouragementReview");
		methods.add("changeEncouragementStatus");


		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");

		String prompt =
				"You are an expert Java analyst.\n" +
						"\n" +
						"Given a List<String> named methods and a natural language question.\n" +
						"\n" +
						"Task:\n" +
						"- Identify the method name that directly relates to the action described in the question.\n" +
						"- Only consider method names containing the literal word 'commission' or obvious business synonyms of 'send to commission'.\n" +
						"- DO NOT assume, guess, or match based on unrelated words like 'sent', 'update', 'review'.\n" +
						"- OUTPUT MUST BE ONLY the method name, NOTHING ELSE.\n" +
						"- Do NOT include explanations, reasoning, prefixes, or any text before or after the method name.\n\n" +
						"\n" +
						"methods = " + methods.toString() + "\n" +
						"User question: \"" + question + "\"\n";



		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println(response.getBody().getResponse());
			return response.getBody().getResponse();
//			methodsList.add(method.split("\n")[0].trim().concat(response.getBody().getResponse()));
//			XWPFParagraph titlePara = document.createParagraph();
//			titlePara.createRun().setBold(true);
//			// اضافه کردن توضیح متد
//			XWPFParagraph paragraph = document.createParagraph();
//			paragraph.createRun().setText(response.getBody().getResponse());
		}

		return url;
	}

	public static ResponseEntity<byte[]> renderMermaidToPng(String mermaidCode) throws IOException, InterruptedException {
		// 1. ذخیره کد Mermaid در فایل موقت
		File tempFile = File.createTempFile("diagram", ".mmd");
		try (FileWriter writer = new FileWriter(tempFile)) {
			writer.write(mermaidCode);
		}

		// 2. تولید تصویر PNG با Mermaid CLI
		File outputFile = File.createTempFile("diagram", ".png");


		ProcessBuilder pb = new ProcessBuilder(
				"C:\\Users\\user1224\\AppData\\Roaming\\npm\\mmdc.cmd",
				"-i", tempFile.getAbsolutePath(),
				"-o", outputFile.getAbsolutePath()
		);

		pb.inheritIO(); // for CLI output
		Process process = pb.start();
		int exitCode = process.waitFor();
		if (exitCode != 0) {
			throw new RuntimeException("Mermaid CLI execution failed. Exit code: " + exitCode);
		}

		if (exitCode != 0) {
			tempFile.delete();
			throw new RuntimeException("Mermaid CLI execution failed. Exit code: " + exitCode);
		}

		// 3. خواندن فایل خروجی PNG
		byte[] imageBytes = Files.readAllBytes(outputFile.toPath());

		// 4. پاک کردن فایل‌های موقت
		tempFile.delete();
		outputFile.deleteOnExit();

		// 5. آماده‌سازی ResponseEntity
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.IMAGE_PNG);
		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	public static String getMermaidSvg(String mermaidCode) throws IOException, InterruptedException, NoSuchAlgorithmException, KeyManagementException {
		// Trust all certificates (برای تست)
		SSLContext sslContext = SSLContext.getInstance("TLS");
		sslContext.init(null, new TrustManager[]{new X509TrustManager() {
			public void checkClientTrusted(X509Certificate[] chain, String authType) {}
			public void checkServerTrusted(X509Certificate[] chain, String authType) {}
			public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
		}}, new java.security.SecureRandom());

		HttpClient client = HttpClient.newBuilder()
				.sslContext(sslContext)
				.build();

		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://kroki.io/mermaid/svg"))
				.header("Content-Type", "text/plain")
				.POST(HttpRequest.BodyPublishers.ofString(mermaidCode))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body(); // خروجی SVG
	}

//	public static byte[] svgToPngBytes(String svgContent) throws Exception {
//		PNGTranscoder transcoder = new PNGTranscoder();
//		TranscoderInput input = new TranscoderInput(new java.io.StringReader(svgContent));
//		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//		TranscoderOutput output = new TranscoderOutput(outputStream);
//		transcoder.transcode(input, output);
//		outputStream.flush();
//		return outputStream.toByteArray();
//	}

	public static void previewMermaidLive(String mermaidCode) {
		try {
			// URL Encode کردن کد Mermaid
			String encoded = URLEncoder.encode(mermaidCode, StandardCharsets.UTF_8.toString());
			// لینک به Mermaid Live Editor
			String url = "https://mermaid.live/edit#pako:" + encoded;

			// باز کردن مرورگر ویندوز
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				System.out.println("مرورگر پشتیبانی نمی‌شود. آدرس را باز کنید:");
				System.out.println(url);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void getDiagram(String graph) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");

		body.put("prompt", "" +
				"You are a visual reasoning assistant specialized in diagram rendering.\n" +
				"You will receive as input a complete Mermaid graph definition (starting with 'graph').\n" +
				"Your task is to visually render that diagram exactly as described by the Mermaid syntax.\n" +
				"\n" +
				"Requirements:\n" +
				"1. Do NOT output any text, explanation, or code.\n" +
				"2. Only produce the visual rendering of the Mermaid flowchart.\n" +
				"3. Preserve the direction (LR, TD, etc.), node labels, and edge conditions.\n" +
				"4. Display decision branches (Yes/No, True/False) clearly.\n" +
				"5. Use neat alignment, readable node spacing, and rounded boxes for clarity.\n" +
				"\n" +
				"Input:\n" +
				graph +
				"\n" +
				"Now, render the given Mermaid graph visually."
		);

		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		int i = 1;
		String explanation = null;
		while (i <= 1) {
			ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				explanation = Objects.requireNonNull(response.getBody()).getResponse();

			}
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println(explanation);
			i++;
		}
	}


	private static String
	answerFromCodeWithBlock(String outerMethodName, String codeSnippet, String userQuestion) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");

		String prompt =
				"You are an expert Java business analyst.\n" +
						"\n" +
						"Inputs provided:\n" +
						"1. A complete Java method as a string variable: `" + codeSnippet + "`.\n" +
						"2. A user question in natural language: `" + userQuestion + "`.\n" +
						"\n" +
						"Task:\n" +
						"- Locate the specific block of code (if/else/else if) that is most relevant to the user question.\n" +
						"- ONLY analyze this single relevant block. Do NOT analyze any other code in the method.\n" +
						"- Determine what happens when the condition of that block is true.\n" +
						"- Provide a clear, business-oriented explanation in simple terms.\n" +
						"- If the question relates to an else block, consider the negation of the if condition as part of the logic.\n" +
						"- Ignore all unrelated code and blocks; do not describe, summarize, or reference them in any way.\n" +
						"\n" +
						"Output format (plain text):\n" +
						"********** Outer Method: " + outerMethodName +  "**********" +
						"\"When this condition is met, <business explanation of the effect>\"";

		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		int i = 1;
		String explanation = null;
		while (i <= 1) {
			ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				explanation = Objects.requireNonNull(response.getBody()).getResponse();

			}
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println(explanation);
			i++;
		}

		return explanation;
	}

	private static String answerFromCodeWithBlockAndInnerMethod(String outerMethodName, String innerMethodName, String controllingCondition, String innerCall) {
   		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");

		String prompt =
				"You are an expert Java business logic analyzer.\n" +
						"\n" +
						"You are given:\n" +
						"- The name of an outer method (outerMethodName)\n" +
						"- The name of an inner method that is called inside it (innerMethodName)\n" +
						"- The controlling condition for this call (controllingCondition)\n" +
						"- The exact inner method invocation code (innerCall)\n" +
						"- Optional business definitions for variables (variableDefinitions)\n" +
						"\n" +
						"Your job:\n" +
						"Explain **how and under what condition** the inner method is called inside the outer method.\n" +
						"\n" +
						"Rules:\n" +
						"1. Describe the condition exactly as written in Java (ConditionCode).\n" +
						"2. For the 'Condition' field: BREAK controllingCondition into its individual logical sub-conditions.\n" +
						"   - Output them as a numbered list (1., 2., 3., ...).\n" +
						"   - Each item MUST use the exact Java text of the sub-condition.\n" +
						"   - Do NOT paraphrase, summarize, or modify the condition text.\n" +
						"3. Provide a business interpretation using variableDefinitions (Description).\n" +
						"4. Show the exact method call (Call).\n" +
						"5. If the inner method is called unconditionally, say so.\n" +
						"\n" +
						"Output Format:\n" +
						"********** Outer Method: " + outerMethodName + " **********\n" +
						"{\n" +
						"  \"" + innerMethodName + "\": [\n" +
						"    {\n" +
						"      \"ConditionCode\": \"" + controllingCondition + "\",\n" +
						"      \"Condition\": \"Numbered list of extracted sub-conditions.\",\n" +
						"      \"Description\": \"Business explanation of why this condition exists\",\n" +
						"      \"Call\": \"" + innerCall + "\"\n" +
						"    }\n" +
						"  ]\n" +
						"}\n";



		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		int i = 1;
		String explanation = null;
		while (i <= 1) {
			ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				explanation = Objects.requireNonNull(response.getBody()).getResponse();

			}
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println(explanation);
			i++;
		}

		return explanation;
	}
	private static String answerFromCodeWithInnerMethod(String codeSnippet, String innerMethodName, String question) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");


//		body.put("prompt", "" +
//				"You are a software engineer who reads Java code deeply.\n" +
//				"You are given a full Java method and a user question in natural language.\n" +
//				"\n" +
//				"Task:\n" +
//				"- Describe the behavior of the entire method in human-readable language.\n" +
//				"- For each branch, condition, or loop, explain exactly what happens in that case as: \"If <condition> → <action>\".\n" +
//				"- Only describe what the code explicitly does; do NOT guess business intent.\n" +
//				"- At the end, identify any code blocks that are directly relevant to the user question.\n" +
//				"- Output in JSON format only.\n" +
//				"\n" +
//				"Output format (JSON only):\n" +
//				"{\n" +
//				"  \"methodBehavior\": [\n" +
//				"    \"<description of behavior for first branch>\",\n" +
//				"    \"<description of behavior for second branch>\",\n" +
//				"    \"...\"\n" +
//				"  ],\n" +
//				"  \"relevantToQuestion\": [\n" +
//				"    \"<exact branches/conditions that relate to the user question>\",\n" +
//				"    \"...\"\n" +
//				"  ]\n" +
//				"}\n" +
//				"\n" +
//				"### USER QUESTION:\n" +
//				question + "\n" +
//				"\n" +
//				"### INPUT METHOD:\n" +
//				codeSnippet
//		);

		String prompt =
				"You are an expert Java business logic analyzer.\n" +
						"\n" +
						"You are given:\n" +
						"- A complete Java method (codeSnippet)\n" +
						"- The name of exactly one inner method to analyze (innerMethodName)\n" +
						"- Business definitions for variables (variableDefinitions)\n" +
						"- A user question (userQuestion)\n" +
						"\n" +
						"Your job:\n" +
						"Analyze the ENTIRE outer method and extract **every execution path** where the specified inner method\n" +
						innerMethodName + "\n" +
						"is invoked.\n" +
						"\n" +
						"############################################\n" +
						"### RULES — VERY IMPORTANT (FOLLOW EXACTLY)\n" +
						"############################################\n" +
						"\n" +
						"1. Scan the entire provided Java method from top to bottom.\n" +
						"   Find *every* place where " + innerMethodName + " is called.\n" +
						"\n" +
						"2. For each call:\n" +
						"   - Identify EVERY condition that controls this call.\n" +
						"   - Include ONLY parent conditions **from the root of the method down to the exact call site**.\n" +
						"   - Do NOT include conditions after the call.\n" +
						"   - Do NOT include sibling blocks.\n" +
						"   - Do NOT include unrelated conditions elsewhere in the method.\n" +
						"\n" +
						"3. You MUST include controlling conditions from:\n" +
						"   - if (…) { }\n" +
						"   - else if (…) { }\n" +
						"   - else { } → convert ELSE into the NEGATION of all previous if/else-if conditions in the chain\n" +
						"   - nested if blocks\n" +
						"   - lambda filters and stream filters\n" +
						"   - optional.ifPresent(...) → treat as optional.isPresent()\n" +
						"   - null checks, .isEmpty(), .isPresent(), .size(), comparisons, boolean flags, etc.\n" +
						"\n" +
						"4. For every inner method call, you MUST collect ALL parent conditions from the root of the method down to the exact call site, and combine them using logical AND. Never ignore earlier parent conditions.\n" +
						"\n" +
						"############################################\n" +
						"### CRITICAL RULE — PARENT CONDITIONS ONLY\n" +
						"############################################\n" +
						"\n" +
						"A controlling condition is ONLY a condition that structurally *wraps around* the inner method call in the code (AST parent).\n" +
						"\n" +
						"A condition is considered a parent ONLY IF:\n" +
						"- It appears BEFORE the method call, AND\n" +
						"- Its block { ... } directly CONTAINS the method call (the call is inside its braces), AND\n" +
						"- Program execution MUST pass through that condition to reach the method call.\n" +
						"\n" +
						"If the inner method call is not inside any if/else/condition block, you MUST output: \n" +
						"   ConditionCode: <none>\n" +
						"   Condition: this method is executed unconditionally. For business purposes, this means the method can be called directly from the frontend.\n" +
						"\n" +
						"The following MUST NOT be treated as controlling conditions:\n" +
						"- Any IF / ELSE IF / ELSE that appears AFTER the method call inside the same block.\n" +
						"- Any nested IF that begins AFTER the method call.\n" +
						"- Any condition that does NOT structurally wrap the call.\n" +
						"\n" +
						"You must ignore all conditions that appear after the call, even if they are in the same brace level.\n" +
						"\n" +
						"### IMPORTANT EXAMPLES:\n" +
						"Example 1 (valid parents):\n" +
						"if (A) {\n" +
						"    if (B) {\n" +
						"        innerMethod();   // controlling conditions: A, B\n" +
						"    }\n" +
						"}\n" +
						"Example 2 (NOT parents):\n" +
						"if (A) {\n" +
						"    innerMethod();       // controlling condition: ONLY A\n" +
						"    if (B) {\n" +
						"        ...              // B is NOT a controlling condition\n" +
						"    }\n" +
						"}\n" +
						"Example 3 (user's code scenario):\n" +
						"if (...) {\n" +
						"    this.calculator(...);   // controlling conditions: ONLY the IFs before this call\n" +
						"    if (encouragement.getEncouragementStatus().equals(...) ||\n" +
						"        encouragement.getEncouragementStatus().equals(...)) {\n" +
						"        // THIS IF must NOT be included as a parent condition.\n" +
						"    }\n" +
						"}\n" +
						"4. Additional strict rule:\n" +
						"   - If a condition contains a logical negation (!) such as:\n" +
						"       if (!approvedNextEncouragementReview.isEmpty())\n" +
						"     You must explicitly interpret and describe it as the NEGATION of the base condition.\n" +
						"     Example: !list.isEmpty() → list.isEmpty() == false.\n" +
						"\n" +
						"5. Optional structures:\n" +
						"   - optional.ifPresent(x -> innerMethod(x)) → condition: optional.isPresent()\n" +
						"   - Calls inside lambdas MUST be detected and counted.\n" +
						"   - Stream API must NOT hide the method calls.\n" +
						"\n" +
						"6. For each inner method call, output:\n" +
						"   - All controlling conditions above it\n" +
						"   - Each condition EXACTLY as written in Java (ConditionCode)\n" +
						"   - A plain-English logical explanation (Condition)\n" +
						"   - A business interpretation using variableDefinitions (Description)\n" +
						"   - The exact method invocation with parameters (Call)\n" +
						"\n" +
						"7. If a call is inside an ELSE block:\n" +
						"   - DO NOT include the original positive if conditions\n" +
						"   - Instead, include the clean logical NEGATION of all previous conditions in the chain\n" +
						"   - Apply De Morgan's law for compound conditions\n" +
						"   - Provide both Java-style negation AND plain-English meaning of the negation\n" +
						"\n" +
						"8. Strict Output Format:\n" +
						"********** Outer Method: <outerMethodName> **********\n" +
						"{\n" +
						"  \"" + innerMethodName + "\": [\n" +
						"    {\n" +
						"      \"ConditionCode\": \"<Java condition>\",\n" +
						"      \"Condition\": \"<plain English logical meaning>\",\n" +
						"      \"Description\": \"<business explanation>\",\n" +
						"      \"Call\": \"<actual method call>\"\n" +
						"    }\n" +
						"  ]\n" +
						"}\n" +
						"\n" +
						"9. Do not merge separate execution paths.\n" +
						"10. Never omit any call.\n" +
						"11. Finally, answer the userQuestion.\n" +
						"\n" +
						"############################################\n" +
						"### NOW ANALYZE THE FOLLOWING INPUTS:\n" +
						"############################################\n" +
						"\n" +
						"codeSnippet:\n" +
						codeSnippet + "\n" +
						"\n" +
						"innerMethodName:\n" +
						innerMethodName + "\n" +
						"\n" +
						"variableDefinitions:\n" +
						"{\n" +
						"  managerPositionList: \"An ordered hierarchy of all manager positions\",\n" +
						"  creatorPositionInListOfManagersPosition: \"The job position code of the reviewer if found in manager positions\",\n" +
						"  creatorPosition: \"The actual job position title of the reviewer\",\n" +
						"  creatorIndex: \"The position of the reviewer in the hierarchy\"\n" +
						"}\n" +
						"\n" +
						"userQuestion:\n" +
						question;



//		String prompt="" +
//				"You are a precise Java code path analyzer.\n" +
//				"Your task is to analyze exactly under what conditions an inner method is called inside another method.\n" +
//				"\n" +
//				"INPUTS:\n" +
//				"1. MAIN_METHOD:\n" +
//				codeSnippet +
//				"\n" +
//				"2. INNER_METHOD_NAME:\n" +
//				innerMethodName +
//				"\n" +
//				"3. QUESTION:\n" +
//				question +
//				"\n" +
//				"4. VARIABLE DEFINITIONS (for business interpretation):\n" +
//				"- managerPositionList: An ordered hierarchy of all manager positions.\n" +
//				"- creatorPositionInListOfManagersPosition: The job position code of the review creator, if found in the list of manager positions.\n" +
//				"- creatorPosition: The actual job position title based on the creatorPositionInListOfManagersPosition.\n" +
//				"- creatorIndex: The position of the current manager in the hierarchy.\n" +
//				"\n" +
//				"GUIDELINES:\n" +
//				"- List ALL conditions required for the inner method to be called.\n" +
//				"- Show the EXACT logical path (if/else) leading to the call.\n" +
//				"- If multiple branches call the method, list each branch separately.\n" +
//				"- If the method is called with a specific parameter value (e.g., APPROVED), analyze only branches that use that value.\n" +
//				"- Do NOT summarize. Provide precise boolean conditions.\n" +
//				"\n" +
//				"IMPORTANT RULE:\n" +
//				"- If the target inner method appears inside an `else` block, you MUST use the negation of the corresponding `if` condition.\n" +
//				"- Example:\n" +
//				"      if (X) { ... } else { call M(); }\n" +
//				"  Means:\n" +
//				"      M() is called when NOT(X).\n" +
//				"- Never state that “X must be true” if the method is inside an else.\n" +
//				"- Always explicitly convert the IF condition into its negated form with correct boolean logic.\n" +
//				"- Treat chained ELSE IF blocks the same way.\n" +
//				"\n" +
//				"OUTPUT STRUCTURE:\n" +
//				"1. List of all branches where the INNER_METHOD_NAME is executed.\n" +
//				"2. For each branch: exact required conditions.\n" +
//				"3. The minimal boolean expression that must be TRUE for the call to happen.\n" +
//				"4. The specific line of code that triggers the call.\n" +
//				"\n" +
//				"Now start the analysis.\n";



		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		int i = 1;
		String explanation = null;
		while (i <= 1) {
			ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				explanation = Objects.requireNonNull(response.getBody()).getResponse();

			}
			System.out.println("----------------------------------------------------------------------------------------");
			System.out.println(explanation);
			i++;
		}

		return explanation;
	}


	public static List<Double> getEmbedding(String text) throws Exception {
		String requestBody = String.format("""
        {
          "model": "llama3",
          "input": %s
        }
        """, toJsonString(text));

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("http://localhost:11434/api/embed"))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(requestBody))
				.build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

		if (response.statusCode() == 200) {
			Gson gson = new Gson();
			Map<String, Object> map = gson.fromJson(response.body(), Map.class);

			// فیلد embedding داخل map با کلید "embedding" یا "embeddings" برمی‌گردد
			List<Double> embedding = (List<Double>) ((List<?>) map.get("embeddings")).get(0);
			return embedding;
		} else {
			throw new RuntimeException("خطا در دریافت embedding: " + response.statusCode() + "\n" + response.body());
		}
	}


	public static double cosineSimilarity(List<Double> v1, List<Double> v2) {
		double dot = 0.0, norm1 = 0.0, norm2 = 0.0;
		for (int i = 0; i < v1.size(); i++) {
			double a = v1.get(i);
			double b = v2.get(i);
			dot += a * b;
			norm1 += a * a;
			norm2 += b * b;
		}
		return dot / (Math.sqrt(norm1) * Math.sqrt(norm2));
	}

	private static String toJsonString(String text) {
		return "\"" + text
				.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\n", "\\n")
				.replace("\r", "\\r") + "\"";
	}

	private static String getIfBusiness(String codeSnippet) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");

		body.put("prompt", ""
				+ "You are an expert Java business analyst.\n"
				+ "You are given a single Java `if` condition from production code.\n"
				+ "Your tasks:\n"
				+ "1. Analyze what this condition checks for in pure logical and business terms.\n"
				+ "2. Avoid technical jargon (like `equals`, `null`, `boolean`) — use business-oriented language instead.\n"
				+ "3. Do NOT guess purpose — describe only what is *explicitly* present in code.\n"
				+ "4. Identify the most important field involved in this condition (not just the entity) and output it as a single keyword at the end.\n"
				+ "Output strictly in JSON format followed by a tag line:\n"
				+ "{\n"
				+ "  \"businessExplanation\": \"<clear business meaning of the condition>\",\n"
				+ "  \"mainEntity\": \"<the single most important field involved in this condition>\"\n"
				+ "}\n"
				+ "**************<mainEntity>*****************\n"
				+ "\n"
				+ "Input:\n"
				+ codeSnippet + "\n"
		);


		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		String explanation = null;

		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			return Objects.requireNonNull(response.getBody()).getResponse();
		}
		return null;
	}
	private static String getBusinessWithInnerMethod(String codeSnippet) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");
		body.put("prompt", "" +
				"You are an expert business analyst and Java code analyzer. " +
				"You will be given the full Java code of the method `addOrUpdateEncouragement`. " +
				"Your tasks:\n" +
				"1. Analyze the code and automatically generate a **complete Mermaid flowchart** (`graph LR`) representing the business logic.\n" +
				"2. Include all `if` / `else` conditions with their real variable names.\n" +
				"3. Include all loops (`for`, `foreach`, `while`) with iteration logic.\n" +
				"4. Include all internal method calls, like `calculator`, annotated with their business role.\n" +
				"5. For each variable in the flow, explain how it is calculated or assigned, either from a method return value, parameter, or computed logic.\n" +
				"6. Use exact variable and method names from the code.\n" +
				"7. Do not invent steps or add explanations outside the flowchart.\n" +
				"8. Ensure the output is fully renderable in standard Mermaid syntax.\n\n" +
				"Output format:\n" +
				"- Start with `graph LR`\n" +
				"- Annotate each action with a brief note on variable calculations using parentheses, e.g., `A[calculate total -> totalAmount = sum(values)]`\n" +
				"- Represent all branching, loops, and method calls accurately.\n\n" +
				"Here is the Java method code:\n" +
				"```\n" +
				codeSnippet +
				"```\n\n" +
				"Generate the Mermaid flowchart automatically based on this code, including all variable computation details."
		);

		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		String explanation = null;

		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			explanation = Objects.requireNonNull(response.getBody()).getResponse();

		}


		Matcher matcher;
		int i = 1;
		final int maxRetries = 1;

		while (i <= maxRetries) {
			boolean hasJavaCode = explanation.matches("(?s).*\\{.*\\}.*|.*;.*");

			if (hasJavaCode) {
				ResponseEntity<HumanResourceRelatedManagerDTO> response1 = restTemplate.exchange(
						url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class
				);

				if (response1.getStatusCode().equals(HttpStatus.OK)) {
					explanation = Objects.requireNonNull(response1.getBody()).getResponse();
					System.out.println("----------------------------------------------------------------------------------------");
					System.out.println(explanation);
				}
			} else {
				Pattern pattern = Pattern.compile("[A-Z]\\s*-->\\s*", Pattern.MULTILINE);
				matcher = pattern.matcher(explanation);

				if (!matcher.find()) {
					ResponseEntity<HumanResourceRelatedManagerDTO> response1 = restTemplate.exchange(
							url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class
					);

					if (response1.getStatusCode().equals(HttpStatus.OK)) {
						explanation = Objects.requireNonNull(response1.getBody()).getResponse();
						System.out.println("----------------------------------------------------------------------------------------");
						System.out.println(explanation);
					}
				} else {
					break;
				}
			}

			i++;
		}

		return explanation;
	}

	private static void writeDoc(XWPFDocument document, String explanation) {
		//		XWPFParagraph paragraph = document.createParagraph();
//		XWPFRun run = paragraph.createRun();
//		run.setBold(true);
//		run.setText(m.getName() + "(");

		// توضیحات مدل داخل پرانتز
		XWPFParagraph explanationPara = document.createParagraph();
		XWPFRun explanationRun = explanationPara.createRun();
		explanationRun.setText(explanation);

//		XWPFParagraph closingPara = document.createParagraph();
//		XWPFRun closingRun = closingPara.createRun();
//		closingRun.setBold(true);
//		closingRun.setText(")");
	}

	public static List<String> searchInDocx(String filePath, String keyword) {
		List<String> matches = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(filePath);
			 XWPFDocument document = new XWPFDocument(fis)) {

			List<XWPFParagraph> paragraphs = document.getParagraphs();

			for (int i = 0; i < paragraphs.size(); i++) {
				String text = paragraphs.get(i).getText();
				if (text != null && text.toLowerCase().contains(keyword.toLowerCase())) {
					matches.add("Paragraph " + (i + 1) + ": " + text);
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return matches;
	}

	public static List<String> splitTextBySentenceExceptSummary(String text) {
		List<String> parts = new ArrayList<>();
		String[] lines = text.split("\\r?\\n");
		boolean inSummary = false;
		StringBuilder summaryBuilder = new StringBuilder();

		for (String line : lines) {
			line = line.trim();
			if (line.isEmpty()) continue;

			// اگر summary شروع شد
			if (line.toLowerCase().startsWith("in summary")) {
				inSummary = true;
			}

			if (inSummary) {
				summaryBuilder.append(line).append(" ");
			} else {
				// split بر اساس جمله‌های معمولی (نقطه، علامت سوال، علامت تعجب)
				String[] sentences = line.split("(?<=[.!?])\\s+");
				for (String s : sentences) {
					s = s.trim();
					if (!s.isEmpty()) parts.add(s);
				}
			}
		}

		// اگر summary وجود داشت، به عنوان یک بخش جدا اضافه کن
		if (summaryBuilder.length() > 0) {
			parts.add(summaryBuilder.toString().trim());
		}

		return parts;
	}

	private static void getFlow() throws Exception {
//		String content = readFromFile("F:/Projects/encouragement-develop/encouragement-service/AllMethodsExplanation16.docx");
//
//
//		Map<String, String> methodMap = new LinkedHashMap<>();
//		Pattern pattern = Pattern.compile(
//				"Method: ([A-Za-z0-9_]+)\\s+(.*?)(?=\\n?Method:|\\Z)",
//				Pattern.DOTALL
//		);
//		Matcher matcher = pattern.matcher(content);
//
//		while (matcher.find()) {
//			String methodName = matcher.group(1).trim();
//			String description = matcher.group(2).trim();
//			methodMap.put(methodName, description);
//		}

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");
//		String methodMapJson = new ObjectMapper().writeValueAsString(methodMap);

//		body.put("prompt",
//				"You are a senior business analyst. You are given an ordered map called `methodMap` where:\n" +
//						"\n" +
//						"- Each key is a method name.\n" +
//						"- Each value is a detailed business explanation of that method (no code, only business meaning, including inner methods).\n" +
//						"\n" +
//						"Your task is to produce a **continuous, step-by-step business process narrative** for the entire encouragement process, starting from creation and ending at final outcomes. Follow these rules strictly:\n" +
//						"\n" +
//						"1. Use **only** the data in methodMap. Do not invent, assume, or add anything.\n" +
//						"2. When a method references another (inner method), immediately inline its explanation exactly as written in methodMap. Repeat recursively if the inner method calls other inner methods.\n" +
//						"3. Present a clear narrative of the process, covering:\n" +
//						"   - How an encouragement is created for an employee.\n" +
//						"   - How and when the status changes.\n" +
//						"   - How internal reviews, manager approvals, corrections, or commission reviews affect the process.\n" +
//						"   - How the encouragement reaches final states such as approved, rejected, sent for Vedja commission, or completed.\n" +
//						"4. Use explicit conditional phrasing where needed, e.g., \"If <condition>, then <result>; otherwise <alternative>.\"\n" +
//						"5. Maintain logical flow and order according to the keys and inner methods in methodMap.\n" +
//						"6. Focus on **business rules and decisions**, not technical implementation.\n" +
//						"7. End with the line: \"All steps above are sourced exclusively from methodMap.\"\n" +
//						"\n" +
//						"Output the narrative in paragraphs or numbered steps.\n" +
//						methodMap
//		);

//		body.put("prompt", """
//				You are a senior business analyst.
//				You will receive a detailed **business-oriented breakdown** of a Java method, including conditional checks and internal method calls.
//
//				Your task:
//				Write **one complete business sentence** summarizing what this method does from start to finish.
//
//				Follow these rules strictly:
//
//				1. **Cover everything** — include all steps, from the first condition to the last one.
//				2. For **every condition**, explicitly describe **what it checks and why it matters**.
//				   - For example: instead of “checking personnel membership codes”, say “checking that personnel membership codes are permanent to ensure only eligible employees are processed.”
//				3. Mention **all important validations**, such as non-null requests, valid IDs, authority limits, and business approvals.
//				4. If the method calls another internal method (e.g. `innerMethods: calculator`), mention it by name and **briefly explain what it does in parentheses**, like:
//				   `(calculator — evaluates authority limits and computes encouragement results)`.
//				5. Summarize all branches and final actions, **including the last conditions** such as “if saved list is non-empty and draft type is ‘Sent’...”.
//				6. Avoid technical or programming terms such as "if", "loop", "variable", or "throw".
//				7. The output must be **a single sentence**, business-focused, clear, and complete.
//
//				Rules:
//				- Your summary must reflect **every line and condition mentioned**, including the last ones.
//				- Conditions and validation phrases should be written as business rules, e.g.:
//				  - “only when the request has non-null personnel organization IDs and valid reasons,”
//				  - “proceeding only if membership codes are permanent and the manager exists with matching organization IDs,”
//				  - “and finally, when the saved list is non-empty and draft type is 'Sent', calling calculator (calculator — computes final encouragement results).”
//				- The output must sound like an end-to-end business flow description.
//
//				Additional clarity rule:
//				- For every condition, also state what happens when it **is not satisfied** (e.g. “otherwise, the process is halted and an error is raised” or “the request is rejected and no further action occurs”).
//				- Do **not** skip or generalize any condition using vague phrases like “specific conditions” or “various cases”.
//				- Always show how each rule affects whether the business process continues or stops.
//
//				Follow these rules strictly:
//
//				Business sentence rules:
//				- Include all **business-relevant steps and conditions** that affect decisions or outcomes.
//				- Ignore trivial checks like non-null values.
//				- For every business-relevant condition, explain **what it checks, why it matters, and what happens if it fails**.
//				- Mention internal methods from `innerMethods:` and their business role.
//				- Output must be **a single sentence**, business-focused, and complete.
//
//				Flowchart rules:
//				- Use **Mermaid syntax** for the flowchart.
//				- Include all decisions and branches.
//				- Indicate where internal methods are invoked and their business role in parentheses.
//				- Ensure the flowchart can be rendered by standard Mermaid tools.
//
//				Now summarize the following method in one sentence and draw a Mermaid flowchart:
//				---
//				""" + methodDescription);


		String codeSnippetOrFlowchart = "addOrUpdateEncouragement:\n" +
				"A[Request validation] -->|true|> B[Validate request amount and reason type]\n" +
				"B -->|complete|> C[Iterate through each personnel organization ID]\n" +
				"C -->|each ID|> D[Find encouraged person by organization ID]\n" +
				"D -->|found|> E[Check if encouraged person's membership code is permanent]\n" +
				"E -->|true|> F[Create encouragement object and set status to draft]\n" +
				"F -->|complete|> G[Save all encouragements to the database and update their statuses]\n" +
				"H[Encouragement draft check] -->|sent|> I[System will proceed to process the encouragement draft]\n" +
				"J[Encouragement seen check] -->|false|> K[Perform calculations and updates relevant fields for the encouragement]\n" +
				"K -->|complete|> L[Create a new review or update an existing one]\n" +
				"M[Review list check] -->|empty|> N[Create a new review]\n" +
				"N -->|complete|> O[Call calculator method to perform calculations]\n" +
				"O -->|complete|> P[Update the encouragement status and call calculator method when encouragement is sent for registrar correction or needs acceptance]\n" +
				"\n" +
				"calculator:\n" +
				"A[Encouragement exists] -->|Check personnel review creator|> B(Personnel review creator exists)\n" +
				"B -->|Check registrar power limits|> C(Checking registrar power limits)\n" +
				"C -->|Current user is encouragement specialist|> D(Allow Vedja commission processing)\n" +
				"D -->|Check registrar power limits (again)|> E(Power limits check result)\n" +
				"E -->|Result within power limits|> F(Approve encouragement)\n" +
				"F -->|Manager and draft sent|> G(Remove encouragement)\n" +
				"G -->|No manager or draft not sent|> H(Throw GeneralException)\n" +
				"C -->|Current user is not encouragement specialist|> I(Check registrar power limits again)\n" +
				"I -->|Optional registrar power limits present|> J(Power limits check result 2)\n" +
				"J -->|Result within power limits|> K(Approve encouragement)\n" +
				"K -->|Manager and draft sent|> L(Remove encouragement)\n" +
				"L -->|No manager or draft not sent|> M(Throw GeneralException)\n" +
				"I -->|Optional registrar power limits not present|> N(Throw GeneralException)\n" +
				"F -->|Inner method call: afterCheckingRegistrarPowerLimit|> H\n" +
				"H -->|GeneralException|> E\n" +
				"\n" +
				"afterCheckingRegistrarPowerLimit:\n" +
				"A[Start]\n" +
				"B(Not Update Mode) -->|createReviewFromEncouragement|(C[Create Review])\n" +
				"D[Update Mode] -->|No Action|(E[Throw Exception])\n" +
				"F[Check Manager & Draft] -->|forwardEncouragementForNextStep|(G[Forward Encouragement])\n" +
				"H[Manager not found or draft is not sent] -->|Throw GeneralException|(I[Error])\n" +
				"\n" +
				"forwardEncouragementForNextStep:\n" +
				"A[Start] -->|Check if current user is Encouragement Specialist or creator not in managers list|\n" +
				"B{condition true}\n" +
				"B -->|Commission input for encouragement|\n" +
				"C[Condition false]\n" +
				"C -->|Get creator position in manager's list and check next/previous manager|\n" +
				"D[Check if next manager exists] -->|Forward to next manager|\n" +
				"E[Check if previous manager exists] -->|Forward to previous manager|\n" +
				"\n" +
				"forwardNextManager:\n" +
				"A[Delete Recent Manager Review] -->|True|> B[Delete Registrar Review]\n" +
				"C[Creator Index + 1 < Manager Position List Size] -->|True|> D[Get Next Manager Organization ID]\n" +
				"E[Get Previous Manager Organization ID (if possible)] -->|Present|> F[Delete Previous Encouragement Review]\n" +
				"G[Next Encouragement Review is Present] -->|True|> H[Check if Encouragement Status is Rejected or Approved]\n" +
				"I[Encouragement Status not Rejected nor Approved] -->|True|> J[Create Review for Next Manager]\n" +
				"K[Else (Not Rejected nor Approved)] -->|Present|> L[Update Exist Encouragement Review for this Encouragement]\n" +
				"M[Check if Commission Input is Required] -->|True|> N[Commission Input]\n" +
				"O[Optional Encouragement Review is Present] -->|True|> P[Delete Commission Review]\n" +
				"Q[Else (No Commission Review)] -->|True|> R[Update Exist Encouragement Review for this Encouragement]" +
				"\n" +
//				"forwardPreviousManager:\n" +
				"";
		body.put("prompt", """
				You are an AI model that should only **echo** the provided input exactly as it is, without adding, removing, reformatting, or commenting on anything.

				Rules:
				- Do not analyze.
				- Do not summarize.
				- Do not translate.
				- Do not explain.
				- Just print back the exact input below — character by character.

				Input:
				---
				""" + codeSnippetOrFlowchart + """
				---
				Output (exactly the same as input above):
				""");


		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			System.out.println("----------------------------------------------------------");
			System.out.println(response.getBody().getResponse());
			translator(response.getBody().getResponse());
//			XWPFParagraph titlePara = document.createParagraph();
//			titlePara.createRun().setBold(true);
//			// اضافه کردن توضیح متد
//			XWPFParagraph paragraph = document.createParagraph();
//			paragraph.createRun().setText(response.getBody().getResponse());
		}

	}

		public static void translator(String text) throws Exception {
			String langFrom = "en";
			String langTo = "fa";

			String urlStr = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
					+ langFrom + "&tl=" + langTo + "&dt=t&q=" + URLEncoder.encode(text, "UTF-8");

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("User-Agent", "Mozilla/5.0");

			try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
				StringBuilder response = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) response.append(line);
				String result = new JSONObject("{\"result\":" + response + "}")
						.getJSONArray("result")
						.getJSONArray(0)
						.getJSONArray(0)
						.getString(0);
				System.out.println("Translated: " + result);
			}
		}

	private static String getBusiness(String method) throws IOException {
//		String json = Files.readString(Path.of("F:/Projects/encouragement-develop/encouragement-service/methods.json"));

		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");
//		body.put("prompt", "Would you please explain this Java code in plain human language to understand the business?Please explain this Java code in plain human language, focusing ONLY on the **business logic and decision-making**. \n" +
//				"Do NOT explain method parameters, variable values, or the inputs of any called methods. \n" +
//				"Just describe the conditions being checked so carefully and do not miss any condition in complex if statement and the **general action taken** in the THEN or ELSE blocks, e.g., \"perform a calculation\" or \"change the status\", without listing the arguments." +
//				"Analyze the code step by step starting from the innermost if-else blocks. For each inner block, determine the action taken. Then move outward to the outer blocks, combining the results to get the final business logic\n"
//				+ codeSnippet);
		body.put("prompt",
				"tell me what does this code do:" +
						method
		);

		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println(response.getBody().getResponse());
			return response.getBody().getResponse();
//			methodsList.add(method.split("\n")[0].trim().concat(response.getBody().getResponse()));
//			XWPFParagraph titlePara = document.createParagraph();
//			titlePara.createRun().setBold(true);
//			// اضافه کردن توضیح متد
//			XWPFParagraph paragraph = document.createParagraph();
//			paragraph.createRun().setText(response.getBody().getResponse());
		}

		return url;
	}

	public static String extractSummary(String methodDoc) {
		int summaryStart = methodDoc.indexOf("In summary");
		if (summaryStart == -1){
				return generateSummaryWithLLaMA(methodDoc);
		}
		int summaryEnd = methodDoc.lastIndexOf(")");
		if (summaryEnd == -1 || summaryEnd <= summaryStart) summaryEnd = methodDoc.length(); // اگر پرانتز نبود

		// استخراج و trim کردن متن summary
		return methodDoc.substring(summaryStart , summaryEnd).trim();
	}

	public static void extractMethodJson(ArrayNode arrayNode, ObjectMapper mapper, String methodDoc, Set<String> entryPointServices) {
		// Create Jackson objects
		// Split the method name from the rest
		String methodName = extractMethodName(methodDoc);

		// Extract summary
		String summary = extractSummary(methodDoc);

		// Create JSON object
		ObjectNode methodNode = mapper.createObjectNode();
		methodNode.put("methodName", methodName);
		if (entryPointServices.contains(methodName))
			methodNode.put("isEntryPoint", true);
		methodNode.put("summary", summary);
		Set<String> call = extractCall(methodDoc, methodName);
		ArrayNode putArray = methodNode.putArray("calls");// empty array for now
		call.forEach(putArray::add);

		// Add to array
		arrayNode.add(methodNode);
	}

	private static String extractMethodName(String methodDoc) {
		int nameEnd = methodDoc.indexOf("(");
		if (nameEnd == -1) {
			throw new IllegalArgumentException("Invalid method doc format");
		}
		List<String> methodNameList= new ArrayList<>();
		String methodName = methodDoc.substring(0, nameEnd).trim();
		methodNameList.add(methodName);
		return methodName;
	}

	private static Set<String> extractCall(String methodDoc, String methodName) {
		Set<String> set= new HashSet<>();
		String[] words = methodDoc.split("\\W+");
		for (String word : words) {
			if (methodsNameFromFile.contains(word) && !word.equals(methodName)) {
				set.add(word);
			}
		}
		return set;
	}

	public static List<String> extractMethodsFromFile(String filePath) throws IOException {
		List<String> methods = new ArrayList<>();

		String content = readFromFile(filePath);
		Pattern pattern = Pattern.compile("(?m)(\\w+)\\((.*?)(?=^\\))", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(content);

		List<String> methodBlocks = new ArrayList<>();

		while (matcher.find()) {
			String methodName = matcher.group(1).trim();
			String methodBody = matcher.group(2).trim();
			methodBlocks.add(methodName + "(\n" + methodBody + "\n)");
		}
		return methodBlocks;
	}

	private static String readFromFile(String filePath) throws IOException {
		File file = new File(filePath);
		FileInputStream fis = new FileInputStream(file);
		XWPFDocument document = new XWPFDocument(fis);

		StringBuilder sb = new StringBuilder();
		for (XWPFParagraph para : document.getParagraphs()) {
			sb.append(para.getText()).append("\n");
		}

		String content = sb.toString();
		return content;
	}

	private static String generateSummaryWithLLaMA(String text) {
		try {

			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/json");
			Map<String, Object> body = new HashMap<>();
			body.put("model", "llama3");
			String prompt =
					"You are a senior business analyst who explains Java methods as business logic, not as code.\n" +
							"Summarize the following Java method in clear and precise business statements.\n" +
							"Focus only on what the method does from a business or process perspective — not how it is implemented.\n" +
							"Start your summary with 'In summary' and be specific, concise, and professional.\n\n" +
							text;

			body.put("prompt", prompt);
			body.put("stream", false);
			String url = "http://localhost:11434/api/generate";
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
			ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
			if (response.getStatusCode().equals(HttpStatus.OK)) {
				String response1 = response.getBody().getResponse();
				int summaryStart = response1.indexOf("In summary");
				System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
				return response1.substring(summaryStart).trim();
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return "Failed to generate summary with LLaMA3.";
		}
	}

	public static Map<String, List<String>> extractServiceCalls(String controllerFilePath, String serviceFieldName) throws Exception {
		File file = new File(controllerFilePath);
		JavaParser parser = new JavaParser();  // ✅ ایجاد instance
		CompilationUnit cu = parser.parse(file).getResult().orElseThrow(() -> new RuntimeException("Parse error"));

		Map<String, List<String>> controllerToServiceCalls = new LinkedHashMap<>();

		cu.findAll(com.github.javaparser.ast.body.MethodDeclaration.class).forEach(method -> {
			List<String> serviceCalls = new ArrayList<>();
			method.findAll(com.github.javaparser.ast.expr.MethodCallExpr.class).forEach(call -> {
				if (call.getScope().isPresent() && call.getScope().get().toString().equals(serviceFieldName)) {
					serviceCalls.add(call.getNameAsString());
				}
			});
			if (!serviceCalls.isEmpty()) {
				controllerToServiceCalls.put(method.getNameAsString(), serviceCalls);
			}
		});

		return controllerToServiceCalls;
	}

	private static void extractMethod(String methodName) throws IOException {
		SourceRoot sourceRoot = new SourceRoot(Paths.get("F:\\Projects\\encouragement-develop\\encouragement-service\\src\\main\\java\\com\\paya\\EncouragementService\\service"));
		Map<String, CompilationUnit> allClasses = new HashMap<>();
		Set<String> wantedClasses = Set.of("EncouragementService", "EncouragementReviewService");
//
		sourceRoot.tryToParse().forEach(result -> result.ifSuccessful(cu -> {
			cu.getPrimaryTypeName().ifPresent(name -> {
				if (wantedClasses.contains(name)) {
					allClasses.put(name, cu);
				}
			});
		}));

		sourceRoot.tryToParse().forEach(r -> r.ifSuccessful(cu -> allClasses.put(cu.getPrimaryTypeName().orElse("Unknown"), cu)));
		XWPFDocument document = new XWPFDocument();
		for (Map.Entry<String, CompilationUnit> entry : allClasses.entrySet()) {
			String className = entry.getKey();
			CompilationUnit cu = entry.getValue();

			System.out.println("Class: " + className);

			cu.getTypes().forEach(type -> {
				if (type.isClassOrInterfaceDeclaration()) {
					ClassOrInterfaceDeclaration cls = type.asClassOrInterfaceDeclaration();
					// Methods
					cls.getMethods().forEach(m -> {
						if (m.getName().toString().equals(methodName)) {
							String codeSnippet = m.toString();
							String explanation = getBusinessWithInnerMethod(codeSnippet);
							System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
							System.out.println(explanation);
							writeDoc(document, explanation);
						}
					});
				}
//		public void askLLMA () throws Exception {
			});
		}
	}


	public static Map<String, List<Map<String, Object>>> extractIfElseLinesMap(List<MethodDeclaration> methods) {
		Map<String, List<Map<String, Object>>> linesMap = new LinkedHashMap<>();

		for (MethodDeclaration method : methods) {
			method.accept(new VoidVisitorAdapter<Deque<Expression>>() {
				Deque<Expression> currentStack;

				@Override
				public void visit(IfStmt ifStmt, Deque<Expression> parentConditions) {

//					List<String> targetMethods = Arrays.asList(
//							"updateEachEncouragementReviewThatNeeded",
//							"updateEncouragementReview",
//							"forwardNextManager",
//							"sentForRegistrar",
//							"deleteRegistrarReviewIfExist",
//							"forwardPreviousManager",
//							"commissionInput",
//							"afterCheckingRegistrarPowerLimit",
//							"forwardEncouragementForNextStep",
//							"calculator",
//							"addOrUpdateEncouragement"
//					);

					String currentMethodName = method.getNameAsString();

//					if (targetMethods.contains(currentMethodName)) {

						// کپی استک والد برای این شاخه
						currentStack = new ArrayDeque<>(parentConditions);

						// شرط این IF را به استک اضافه کن
						currentStack.push(ifStmt.getCondition());

						// THEN block
						if (ifStmt.getThenStmt().isBlockStmt()) {
							for (Statement stmt : ifStmt.getThenStmt().asBlockStmt().getStatements()) {
								visitStatement(stmt, currentStack, method.getNameAsString());
							}
						} else {
							visitStatement(ifStmt.getThenStmt(), currentStack, method.getNameAsString());
						}

						// ELSE block
						ifStmt.getElseStmt().ifPresent(elseStmt -> {
							// شرط منفی ELSE
							Expression negated = new UnaryExpr(ifStmt.getCondition(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
							Deque<Expression> elseStack = new ArrayDeque<>(parentConditions);
							elseStack.push(negated);

							if (elseStmt.isBlockStmt()) {
								for (Statement stmt : elseStmt.asBlockStmt().getStatements()) {
									visitStatement(stmt, elseStack, method.getNameAsString());
								}
							} else {
								visitStatement(elseStmt, elseStack, method.getNameAsString());
							}
						});

						super.visit(ifStmt, parentConditions);
//					}
				}

				private void visitStatement(Statement stmt, Deque<Expression> conditionStack, String currentMethodName) {

					if (stmt.isExpressionStmt() && stmt.asExpressionStmt().getExpression().isMethodCallExpr()) {

						MethodCallExpr call = stmt.asExpressionStmt().getExpression().asMethodCallExpr();

						// یک ID یکتا برای این کال (برای تشخیص اینکه همان کال است)
						String callId = call.getBegin()
								.map(p -> p.line + ":" + p.column)
								.orElse(call.toString());

						// شروط جدید
						List<String> newConditions = conditionStack.stream()
								.map(Expression::toString)
								.toList();

						// گرفتن لیست رکوردهای این متد
						List<Map<String, Object>> methodRecords =
								linesMap.computeIfAbsent(currentMethodName, k -> new ArrayList<>());

						// پیدا کردن این کال (اگر قبلا ثبت شده)
						Optional<Map<String, Object>> existingOpt = methodRecords.stream()
								.filter(r -> r.get("callId").equals(callId))
								.findFirst();

						if (existingOpt.isPresent()) {
							Map<String, Object> existing = existingOpt.get();
							List<String> oldConditions = (List<String>) existing.get("conditions");

							// اگر قبلی کامل تر است → جدید را ذخیره نکن
							if (oldConditions.containsAll(newConditions)) {
								return;
							}

							// اگر جدید کامل تر است → جایگزین کن
							if (newConditions.containsAll(oldConditions)) {
								existing.put("conditions", new ArrayList<>(newConditions));
								return;
							}

							// مسیرها متفاوت هستند → باید رکورد جدید ایجاد شود
						}

						// رکورد جدید
						Map<String, Object> record = new LinkedHashMap<>();
						record.put("callId", callId); // برای جلوگیری از تکرار اشتباهی
						record.put("calledMethod", call.toString());
						record.put("conditions", new ArrayList<>(newConditions));

						methodRecords.add(record);
					}

					stmt.accept(this, conditionStack);
				}

			}, new ArrayDeque<>());
		}
		return linesMap;

	}
	static String simplify(String expr) {
		while (expr.contains("!!")) {
			expr = expr.replace("!!", "");
		}
		return expr.trim();
	}

	public static Map<String, List<Double>> getBlocksEmbeddings(Map<String, List<String>> blocksMap) throws Exception {
		Map<String, List<Double>> embeddingsMap = new LinkedHashMap<>();

		for (Map.Entry<String, List<String>> entry : blocksMap.entrySet()) {
			String methodName = entry.getKey();
			List<String> blocks = entry.getValue();

			for (int i = 0; i < blocks.size(); i++) {
				String block = blocks.get(i);
				// کلید = نام متد + شماره بلوک
				String key = methodName + "_block" + (i + 1);

				List<Double> embedding = getEmbedding(block); // متد embedding تو
				embeddingsMap.put(key, embedding);
			}
		}

		return embeddingsMap;
	}

	private static String getSolutionFromMultipleMethods(Map<String, String> businessBlocksMap, String question) throws IOException, InterruptedException {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		Map<String, Object> body = new HashMap<>();
		body.put("model", "llama3");



		String prompt =
				"You are an expert Java business analyst.\n" +
						"\n" +
						"You are given:\n" +
						"1. A Map<String, String> named businessBlocksMap.\n" +
						"   - Each key is in the format: \"<methodName> / if (<condition>) / <lineReference>\"\n" +
						"   - Each value contains a business explanation.\n" +
						"\n" +
						"2. A user question in natural language that may refer to a condition in one of these blocks.\n" +
						"\n" +
						"Your task:\n" +
						"- Identify the single key (method name + if condition + line reference) that is most relevant to the user question.\n" +
						"- ONLY consider keys where the **variables used in the key match the variables explicitly mentioned in the user question.\n" +
						"- Do NOT analyze or include any other blocks that are unrelated.\n" +
						"- Do NOT give any explanations, commentary, or extra text.\n" +
						"- Output exactly one string: the key from the map that matches.\n" +
						"\n" +
						"businessBlocksMap = " + businessBlocksMap.toString() + "\n" +
						"User question: \"" + question + "\"\n";

		body.put("prompt", prompt);
		body.put("stream", false);
		String url = "http://localhost:11434/api/generate";
		HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
		ResponseEntity<HumanResourceRelatedManagerDTO> response = restTemplate.exchange(url, HttpMethod.POST, request, HumanResourceRelatedManagerDTO.class);
		if (response.getStatusCode().equals(HttpStatus.OK)) {
			System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
			System.out.println(response.getBody().getResponse());
			return response.getBody().getResponse();
//			methodsList.add(method.split("\n")[0].trim().concat(response.getBody().getResponse()));
//			XWPFParagraph titlePara = document.createParagraph();
//			titlePara.createRun().setBold(true);
//			// اضافه کردن توضیح متد
//			XWPFParagraph paragraph = document.createParagraph();
//			paragraph.createRun().setText(response.getBody().getResponse());
		}

		return url;
	}
}


